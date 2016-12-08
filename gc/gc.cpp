#include "gc.hpp"
#include "vm.hpp"

using namespace std;

/*
 * What Garbage collection will do
 *
 * "in use" means referenced by a variable in scope
 *  or the obj referenced by another obj that is in use
 *  mark and sweep method
 *  we make a graph of reachable objs
 *  traverse entire obj graph, mark each one as reachable
 *  traverse heap and delete objs that are unreachable
 */

VM::VM() {
    heap.clear();
    garbage_collect = false;
    heap_max = DEFAULT_HEAP_SIZE;
    stack.clear();
}

VM::~VM() {
    /*for (auto pair : heap) {
        free(object);
    }*/
}

VM *vm;

/************ STATIC FUNCTIONS *****************/

static void mark(unsigned long long int heapaddress) {
    object* obj = vm->heap[heapaddress];
    if (obj == NULL) {
        return;
    }
    obj->mark();
}

static void markAll() {
    // Start marking from the stack allocated variables
    for (auto pair : vm->stack) {
        mark(pair.second);
    }
}

static void sweep() {
    vector<unsigned long long int> to_delete;
    to_delete.clear();
    for (auto pair : vm->heap) {
        auto obj = pair.second;
        if (!obj->marked) {
            cout << "Erasing " << pair.first << endl;
            to_delete.push_back(pair.first);
        } else {
            obj->marked = 0;
        }
    }

    // Free all elements
    for (auto key : to_delete) {
        // Delete both the actual memory and the meta-object
        void *addr = reinterpret_cast<void*>(key);
        free(addr);
        free(vm->heap[key]);
    }

    // Update the heap
    for (auto key : to_delete) {
        vm->heap.erase(key);
    }
}

// heuristics of when to call GC
static bool should_collect() {
    return vm->heap.size() >= vm->heap_max / 2;
}


int type_size(object_type type) {
    switch (type) {
      case INT: return 4;
      case PAIR:
      case STRUCT:
      case CLASS:
      case ARRAY: return sizeof(uintptr_t);
      case CHAR:
      case BOOL: return 1;
      case ANY:
      default: return 0;
    }
}

/************ PUBLIC FUNCTIONS *****************/

static void pushHeap(void* heapaddress, object* obj) {
    cout << "Pushing " << heapaddress << " to heap " << endl;
    if (should_collect()) {
        vm->garbage_collect = true;
    }

    obj->marked = 0;
    auto addr = reinterpret_cast<std::uintptr_t>(heapaddress);
    vm->heap[addr] = obj;
}

// call on declaration and assignment
void pushVM(void* stackaddress, void* heapaddress) {
    auto addr1 = reinterpret_cast<std::uintptr_t>(stackaddress);
    auto addr2 = reinterpret_cast<std::uintptr_t>(heapaddress);
    vm->stack[addr1] = addr2;

    if (vm->garbage_collect) {
        vm->garbage_collect = false;
        collect_garbage();
    }
}

uint8_t** new_pair_constructor(object_type type1, object_type type2) {
    // Allocate actual space for the pair and its elements
    uint8_t **bytes = (uint8_t **) malloc(8);
    bytes[0]        = (uint8_t *) malloc(type_size(type1));
    bytes[4]        = (uint8_t *) malloc(type_size(type1));

    // Create meta-objects for the pair and its elements
    pair_object *obj = (pair_object *) object::new_obj(PAIR);
    obj->first       = (pair_object *) object::new_obj(type1);
    obj->second      = (pair_object *) object::new_obj(type2);


    // Map addresses on actual heap to addresses of created meta-objects for the pair
    pushHeap(bytes, obj);
    pushHeap(bytes[0], obj->first);
    pushHeap(bytes[4], obj->second);
    return bytes;
}

uint8_t* new_array_literal(int array_size, object_type type) {
    // Allocate actual space for the array
    uint8_t* bytes = (uint8_t*) malloc(type_size(INT) + array_size * type_size(type));
    cout <<  "Allocated " << type_size(INT) + array_size * type_size(type) << " bytes for array literal" << endl;

    // Create meta-object for the array
    array_object* obj = (array_object*) array_object::new_obj(ARRAY);
    obj->bytes = bytes;
    obj->array_size = array_size;
    obj->array_type = type;

    // Map address on actual heap to address of created meta-object for the array
    pushHeap(bytes, obj);
    return bytes;
}

//called once on startup
void gc_begin() {
    vm = new VM();
}

// free heap
void gc_end() {
    delete vm;
}

/********************* TESTS *************************/
void collect_garbage() {

    //printf("INTTYPE 1\nPAIRTYPE 2\nCHARTYPE 3\nARRAYTYPE 4\nSTRUCTTYPE 5\n");

    printf("\n---------- Before VM heap ---------\n");
    for(auto obj : vm->heap) {
        cout << obj.first << ": " << obj.second->getType() << endl;
    }
    printf("--------------------\n");

    markAll();
    sweep();

    printf("\n---------- After VM heap ---------\n");
    for(auto obj : vm->heap) {
        cout << obj.first << ": " << obj.second->getType() << endl;
    }
    printf("--------------------\n");

}


static void test_int_pair_reassignment() {
    gc_begin();
    uint8_t** o1 = new_pair_constructor(INT, INT);
    uint8_t** o2 = new_pair_constructor(INT, INT);
    pushVM(&o1, o1); // o1 = newpair
    pushVM(&o2, o2); // o2 = newpair
    pushVM(&o2, o1); // o2 = o1

    // o1 should be garbage collected
    auto first_pair_address = reinterpret_cast<std::uintptr_t>(o1);
    auto second_pair_address = reinterpret_cast<std::uintptr_t>(o2);
    auto o1_variable_address = reinterpret_cast<std::uintptr_t>(&o1);
    auto o2_variable_address = reinterpret_cast<std::uintptr_t>(&o2);
    bool test_passed = vm->heap.count(first_pair_address) == 1                        // First pair should still exist in the heap
                       && vm->heap.count(second_pair_address) == 0                    // Second pair should be garbage collected
                       && vm->heap.size() == 3                                        // Thus the heap should contain just 3 objects
                       && vm->stack.size() == 2                                       // The stack should still have 2 mappings
                       && vm->stack[o1_variable_address] == first_pair_address        // The first variable should point to the first pair
                       && vm->stack[o2_variable_address] == first_pair_address;       // and so should the second variable

    cout << "PAIR RE-ASSIGNMENT: " << (test_passed ? "PASSED" : "FAILED") << endl;
    gc_end();
}


static void test_int_array_reassignment() {
    gc_begin();
    uint8_t* o1 = new_array_literal(3, INT);
    uint8_t* o2 = new_array_literal(3, INT);
    pushVM(&o1, o1); // o1 = [1, 2, 3]
    pushVM(&o2, o2); // o2 = [4, 5, 6]
    pushVM(&o2, o1); // o2 = o1
    collect_garbage(); // Have to force it as only 2 heap items are allocated

    // o1 should be garbage collected
    auto first_array_address = reinterpret_cast<std::uintptr_t>(o1);
    auto second_array_address = reinterpret_cast<std::uintptr_t>(o2);
    auto o1_variable_address = reinterpret_cast<std::uintptr_t>(&o1);
    auto o2_variable_address = reinterpret_cast<std::uintptr_t>(&o2);
    bool test_passed = vm->heap.count(first_array_address) == 1                        // First array should still exist in the heap
                       && vm->heap.count(second_array_address) == 0                    // Second array should be garbage collected
                       && vm->heap.size() == 1                                         // Thus the heap should contain just 1 object
                       && vm->stack.size() == 2                                        // The stack should still have 2 mappings
                       && vm->stack[o1_variable_address] == first_array_address        // The first variable should point to the first array
                       && vm->stack[o2_variable_address] == first_array_address;       // and so should the second array
    cout << "INT ARRAY RE-ASSIGNMENT: " << (test_passed ? "PASSED" : "FAILED") << endl;
    gc_end();
}

static void test_pair_array_reassignment() {
    gc_begin();
    uint32_t* o1 = (uint32_t*) new_array_literal(2, PAIR);
    uint32_t* o2 = (uint32_t*) new_array_literal(2, PAIR);
    uint8_t** p1 = new_pair_constructor(INT, INT);
    uint8_t** p2 = new_pair_constructor(INT, INT);
    uint8_t** p3 = new_pair_constructor(INT, INT);
    uint8_t** p4 = new_pair_constructor(INT, INT);

    auto first_array_address = reinterpret_cast<std::uintptr_t>(o1);
    auto second_array_address = reinterpret_cast<std::uintptr_t>(o2);

    auto first_pair_address = reinterpret_cast<std::uintptr_t>(p1);
    auto second_pair_address = reinterpret_cast<std::uintptr_t>(p2);
    auto third_pair_address = reinterpret_cast<std::uintptr_t>(p3);
    auto fourth_pair_address = reinterpret_cast<std::uintptr_t>(p4);

    auto o1_variable_address = reinterpret_cast<std::uintptr_t>(&o1);
    auto o2_variable_address = reinterpret_cast<std::uintptr_t>(&o2);

    o1[0] = 2;
    o1[1] = first_pair_address;
    o1[2] = second_pair_address;

    o2[0] = 2;
    o2[1] = third_pair_address;
    o2[2] = fourth_pair_address;

    /*
    memcpy(o1 + type_size(INT),     p1, 4);
    memcpy(o1 + type_size(INT) + 4, p2, 4);
    memcpy(o2 + type_size(INT),     p3, 4);
    memcpy(o2 + type_size(INT) + 4, p4, 4);
     */

    pushVM(&o1, o1); // o1 = [newpair(1, 1), newpair(2, 2)]
    pushVM(&o2, o2); // o2 = [newpair(3, 3), newpair(4, 4)]
    pushVM(&o2, o1); // o2 = o1

    // o1 should be garbage collected, and so should the pairs contained within it
    bool test_passed = vm->heap.count(first_array_address) == 1                        // First array should still exist in the heap
                       && vm->heap.count(second_array_address) == 0                    // Second array should be garbage collected
                       && vm->heap.count(first_pair_address) == 1
                       && vm->heap.count(second_pair_address) == 1
                       && vm->heap.count(third_pair_address) == 0
                       && vm->heap.count(fourth_pair_address) == 0
                       && vm->heap.size() == 7                                         // Thus the heap should contain 7 objects (2 pairs and an array)
                       && vm->stack.size() == 2                                        // The stack should still have 2 mappings
                       && vm->stack[o1_variable_address] == first_array_address        // The first variable should point to the first array
                       && vm->stack[o2_variable_address] == first_array_address;       // and so should the second array
    cout << "PAIR ARRAY RE-ASSIGNMENT: " << (test_passed ? "PASSED" : "FAILED") << endl;
    gc_end();
}

/*
static void test_complex1_gc() {
    new_pair_constructor(0, -1, -1);
    new_pair_constructor(1, -1, -1);
    new_array_literal(2, 15);
    new_array_literal(3, 129);
    _copy(4, 0);
    _copy(1, 0);
    run_gc();
}

*
 * 4 objs created, id(0) and id(1) point to the same obj
 * 3 objs should be collected, all but the third created
 *
static void test_complex2_gc() {
    new_array_literal(0, 2);
    new_array_literal(1, 2938);
    new_array_literal(0, 10);
    new_array_literal(1, 20);
    _copy(1, 0);
    run_gc();
}
*/

/************ MAIN *****************/
int main() {
    //test_int_pair_reassignment();
    //test_int_array_reassignment();
    test_pair_array_reassignment();
    return 0;
}
