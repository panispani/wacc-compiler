#include "gc.hpp"

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

/************ STATIC FUNCTIONS *****************/


static VM* vm;

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
        cout << "Erasing " << key << endl;
        vm->heap.erase(key);
    }
}

static void gc() {
    markAll();
    sweep();
}

// heuristics of when to call GC
static bool should_gc() {
    return vm->heap.size() >= vm->heap_max / 2;
}


size_t type_size(int type) {
    switch (type) {
      case ANY:
      case INT:
      case PAIR:
      case STRUCT:
      case CLASS:
      case ARRAY: return 4;
      case CHAR:
      case BOOL: return 1;
      default: return 0;
    }
}

/************ PUBLIC FUNCTIONS *****************/

static void pushHeap(void* heapaddress, object* obj) {
    cout << "Pushing " << heapaddress << " -> " << obj << " to heap " << endl;
    if (should_gc()) {
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
        run_gc();
    }
}

uint8_t** new_pair_constructor(object_type type1, object_type type2) {
    // Create meta-objects for the pair and its elements
    pair_object* obj = (pair_object*) object::new_obj(PAIR);
    obj->first = (pair_object*) object::new_obj(type1);
    obj->second = (pair_object*) object::new_obj(type2);

    // Allocate actual space for the pair and its elements
    uint8_t **bytes = (uint8_t**) malloc(8);
    bytes[0] = (uint8_t*) malloc(type_size(type1));
    bytes[4] = (uint8_t*) malloc(type_size(type1));

    // Map addresses on actual heap to addresses of created meta-objects for the pair
    pushHeap(bytes, obj);
    pushHeap(bytes[0], obj->first);
    pushHeap(bytes[4], obj->second);
    return bytes;
}

uint8_t* new_array_literal(int array_size, object_type type) {
    // Create meta-object for the array
    array_object* obj = (array_object*) array_object::new_obj(ARRAY);

    // Allocate actual space for the array
    uint8_t *bytes = (uint8_t*) malloc(4 + array_size * type_size(type));

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
void run_gc() {

    //printf("INTTYPE 1\nPAIRTYPE 2\nCHARTYPE 3\nARRAYTYPE 4\nSTRUCTTYPE 5\n");

    printf("\nBefore VM heap\n");
    for(auto obj: vm->heap) {
        printf("%p of type: not for now\n", obj);
    }

    gc();

    printf("\nAfter VM heap\n");
    for(auto obj: vm->heap) {
        printf("%p of type: not for now\n", obj);
    }

}


static void test_pair_copy_gc() {
    uint8_t** o1 = new_pair_constructor(INT, INT);
    uint8_t** o2 = new_pair_constructor(INT, INT);
    pushVM(&o1, o1); // o1 = newpair
    pushVM(&o2, o2); // o2 = newpair
    pushVM(&o2, o1); // o2 = o1
    // o1 should be garbage collected
}


static void test_array_copy_gc() {
    uint8_t* o1 = new_array_literal(3, INT);
    uint8_t* o2 = new_array_literal(3, INT);
    pushVM(&o1, o1); // o1 = [1, 2, 3]
    pushVM(&o2, o2); // o2 = [4, 5, 6]
    pushVM(&o2, o1); // o2 = o1
    run_gc();
    // o1 should be garbage collected
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
    gc_begin();
    test_array_copy_gc();
    gc_end();
    return 0;
}
