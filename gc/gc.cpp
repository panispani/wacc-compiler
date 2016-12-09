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
    for (auto it : heap) {
        free(it.second);
    }
}

VM *vm;

/************ STATIC FUNCTIONS *****************/

static void mark(unsigned long long int heapaddress) {
    if (!vm->heap.count(heapaddress)) {
        return;
    }
    object* obj = vm->heap[heapaddress];
    if (obj == nullptr) {
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
            //cout << "Erasing " << pair.first << endl;
            to_delete.push_back(pair.first);
        } else {
            obj->marked = 0;
        }
    }

    // Free all elements
    for (auto key : to_delete) {
        // Delete both the actual memory and the meta-object
        void *addr = reinterpret_cast<void*>(key);
        //cout << "Freeing: " << addr << endl;
        free(addr);
        //cout << "Freeing: " << vm->heap[key] << endl;
        free(vm->heap[key]);
        //cout << "Successfully freed" << endl;
    }

    // Update the heap
    for (auto key : to_delete) {
        vm->heap.erase(key);
    }
}

// heuristics of when to call GC
static bool should_collect() {
    // TODO make it real
    //return vm->heap.size() >= vm->heap_max / 2;
    return true;
}


int type_size(object_type type) {
    switch (type) {
      case INT:
      case PAIR:
      case STRUCT:
      case CLASS:
      case PAIR_CONTAINER:
      case ARRAY: return 4;
      case CHAR:
      case BOOL: return 1;
      default: return 0;
    }
}

/************ PUBLIC FUNCTIONS *****************/

static void pushHeap(void* heapaddress, object* obj) {
    //cout << "Pushing " << heapaddress << " to heap " << endl;
    if (should_collect()) {
        vm->garbage_collect = true;
    }

    obj->marked = 0;
    auto addr = reinterpret_cast<std::uintptr_t>(heapaddress);
    vm->heap[addr] = obj;
}


// call on declaration and assignment
void push_stack(void* stackaddress, void* heapaddress) {
    //cout << "Pushing " << stackaddress << " -> " << heapaddress << " to stack " << endl;
    if (heapaddress == nullptr) {
        return;
    }

    auto addr1 = reinterpret_cast<std::uintptr_t>(stackaddress);
    auto addr2 = reinterpret_cast<std::uintptr_t>(heapaddress);

    vm->stack[addr1] = addr2;

    if (vm->garbage_collect) {
        vm->garbage_collect = false;
        collect_garbage();
    }
}

pair_container* new_pair_container(object_type type) {
    // Allocate actual space for the pair container
    uint8_t *bytes = (uint8_t*) malloc(type_size(type));

    // Create meta-object for the pair container
    pair_container *obj = (pair_container *) object::new_obj(PAIR_CONTAINER);
    obj->type = type;
    obj->bytes = bytes;

    // Map addresses on actual heap to addresses of created meta-objects for the pair container
    pushHeap(bytes, obj);
    return obj;
}

uint32_t* new_pair(object_type type1, object_type type2) {
    // Create meta-objects for the pair and its elements
    pair_object *obj = (pair_object *) object::new_obj(PAIR);
    obj->first       = new_pair_container(type1);
    obj->second      = new_pair_container(type2);

    // Allocate actual space for the pair and its elements
    uint32_t **bytes = (uint32_t **) malloc(8);
    bytes[0] = (uint32_t*) obj->first->bytes;
    bytes[1] = (uint32_t*) obj->second->bytes;

    // Map addresses on actual heap to addresses of created meta-objects for the pair
    pushHeap(bytes, obj);
    return (uint32_t*) bytes;
}

uint8_t* new_array_literal(int array_size, object_type type) {
    // Allocate actual space for the array
    uint8_t* bytes = (uint8_t*) malloc(type_size(INT) + array_size * type_size(type));

    // Create meta-object for the array
    array_object* obj = (array_object*) array_object::new_obj(ARRAY);
    obj->bytes = bytes;
    obj->array_size = array_size;
    obj->array_type = type;
    //cout << "Constructing array of size " << array_size << " and type " << type << endl;

    // Map address on actual heap to address of created meta-object for the array
    pushHeap(bytes, obj);
    return bytes;
}

uint8_t* new_struct_literal(int struct_size, int num_types, object_type types[]) {
    // Allocate actual space for the struct
    uint8_t* bytes = (uint8_t*) malloc(struct_size);

    // Create meta-object for the struct
    struct_object* obj = (struct_object*) array_object::new_obj(STRUCT);
    obj->bytes = bytes;
    obj->types = types;
    obj->num_types = num_types;

    // Map address on actual heap to address of created meta-object for the array
    pushHeap(bytes, obj);
    return bytes;
}

// Called once on startup
void gc_begin() {
    vm = new VM();
}

// Called once on shutdown
void gc_end() {
    delete vm;
}


void remove_stack(void* obj) {
    auto addr1 = reinterpret_cast<std::uintptr_t>(obj);
    vm->stack.erase(addr1);
}

void gc_free(void* obj) {
    auto addr1 = reinterpret_cast<std::uintptr_t>(obj);
    delete vm->heap[vm->stack[addr1]];
    vm->heap.erase(vm->stack[addr1]);
    remove_stack(obj);
}

void collect_garbage() {
    printf("\n---------- Before VM heap ---------\n");
    for(auto obj : vm->heap) {
        cout << (void*) obj.first << " -> " << obj.second << " of type " << obj.second->getType() << endl;
     }
    printf("--------------------\n");

    markAll();
    sweep();

    printf("\n---------- After VM heap ---------\n");
    for(auto obj : vm->heap) {
        cout << (void*) obj.first << " -> " << obj.second << " of type " << obj.second->getType() << endl;
    }
    printf("--------------------\n");

}

