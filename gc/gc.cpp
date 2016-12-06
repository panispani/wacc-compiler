#include "gc.hpp"

using namespace std;

/*
 * What Garbage collection will do
 *
 * "in use" means referenced by a variable in scope
 *  or the object referenced by another object that is in use
 *  mark and sweep method
 *  we make a graph of reachable objects
 *  traverse entire object graph, mark each one as reachable
 *  traverse heap and delete objects that are unreachable
 */

static VM* vm;
/************ STATIC FUNCTIONS *****************/

static VM* newVM() {
          VM* vm = (VM*)malloc(sizeof(VM));
          vm->heap.clear();
// declare and assign
          vm->stack.clear();
          return vm;
}

static void mark(Object* object) {
    // printf("marking: %p\n", object);
    if (object == NULL || object->marked) {
        return;
    }
    object->marked = 1;
    switch(object->type) {
    case PAIR:
        mark(object->fields.first);
        mark(object->fields.second);
        break;
    }
    /*case ARRAY:
        ;int i = 0;
        for (; i < object->fields.array_size; i++) {
            mark(object->fields.array[i]);
        }
        break;
    }*/
    // TODO REST
}

static void markAll() {
    // start marking from the stack allocated variables
    for (auto pair: vm->stack) {
        mark(pair.second);
    }
    /*
    for (int i = 0; i < vm->stack_size; i++) {
        //printf("in %p %d\n", vm->stack[i], vm->stack[i]->type);
        mark(vm->stack[i]);
        //printf("out %p\n", vm->stack[i]);
    }
    */
}


// we use pointer to pointer so we can change
// the list(remove element) and it retains its structure!
static void sweep() {
    vector<Object*> to_delete;
    to_delete.clear();
    for (auto object: vm->heap) {
        if (!object->marked) {
            to_delete.push_back(object);
        } else {
            object->marked = 0;
        }
    }
    // we should update heap and free to_delete elements
    vector<Object*> newheap;
    set_difference(vm->heap.begin(), vm->heap.end(),
            to_delete.begin(), to_delete.end(),
            inserter(newheap, newheap.end()));
    swap(vm->heap, newheap);
    for (auto object: to_delete) {
        free(object);
    }
}


/*
  unsigned int current = 0;
  Object** object = &vm->heap[current];
  while (current != vm->heap.size()) {
    if (!(*object)->marked) {
      Object* unreached = *object;
      *object = unreached->next;
      free(unreached);
    } else {
      // object was reached by marking, reset it for next GC
      (*object)->marked = 0;
      object = &(*object)->next;
    }
  }
  */


static void gc() {
    markAll();
    sweep();
}

// heuristics of when to call GC
static int should_gc() {
    // casted to false
    return 0;
}

static Object* new_object() {
        if (should_gc()) {
            gc();
        }
        Object* object = (Object*)malloc(sizeof(Object));
        if (object == NULL) {
            printf("%s\n", "Stack overflow");
            return object;
        }

        object->marked = 0;

        /*
        // append to front of VM object list
        if (vm->head == NULL) {
            object->next = NULL;
        } else {
            object->next = vm->head;
        }
        vm->head = object;
        vm->num_objects = vm->num_objects + 1;
        */
        vm->heap.push_back(object);
        return object;
}

 /*
// look on stack for object with corresponing id
static Object* get_object_with_id(int id) {
        if (id == -1) {
            // not a reference type, shouldn't care
            return NULL; // or int-singleton
        }
        return vm->stack[id];
}
*/

/************ PUBLIC FUNCTIONS *****************/
/*** PAIR ***/
Object* new_pair_constructor() {
    Object* object = new_object();
    object->type = PAIR;
    object->fields.first = new_object();
    object->fields.second = new_object();
    //pushVM(object);
    return object;
}

// call on declaration and assignment
void pushVM(void* stackaddress, Object* object) {
    // can it be repushed? should it be a set
    cout << stackaddress << " " << object << endl;
    auto addr = reinterpret_cast<std::uintptr_t>(stackaddress);
    vm->stack[addr] = object;
    cout << "broken" << endl;
}


/*** ARRAY ***/
// NOTE: dont refactor with string yet
Object* new_array_literal(int id, int size) {
        Object* object = new_object();
        object->type = ARRAY;
        // think of using calloc
        object->fields.array = (Object**)malloc(size * sizeof(Object*));
        memset(object->fields.array, 0, size);
        object->fields.array_size = size;
        //pushVM(object, id);
        return object;
}


/*** STRING ***/
Object* new_string_literal(int id, int size) {
        Object* object = new_object();
        object->type = STRING;
        // think of using calloc
        object->fields.string = (Object**)malloc(size * sizeof(Object*));
        memset(object->fields.string, 0, size);
        object->fields.string_size = size;
        //pushVM(object, id);
        return object;
}

/*** STRUCT ***/

/*** CLASS ***/

/*** COMMON METHODS ***/
// form: foo(dst, src)
// this is ugly and will change in the process of refactoring
/*Object* _copy(int id1, int id2) {
    Object* object = get_object_with_id(id2);
    //pushVM(object);
    return object;
}*/

/* Dont delete yet, creating a new object is wrong
 * delcaring in on the stack, there is no new
 * variable on the heap
Object* declare_copy(int id1, int id2) {
        Object* object = new_object();
        Object* copyfrom = get_object_with_id(id2);
        object->type = copyfrom->type;
        object->fields = copyfrom->fields;
        pushVM(object, id1);
        return object;
}
*/

//called once on startup
void gc_begin() {
    vm = newVM();
}

// free heap
void gc_end() {
    //vm->stack_size = 0;
    //gc();
    //free(vm);
}

/********************* TESTS *************************/
static void run_gc() {

    // subject to change
    printf("INTTYPE 1\nPAIRTYPE 2\nCHARTYPE 3\nARRAYTYPE 4\nSTRUCTTYPE 5\n");

    printf("\nBefore VM heap\n");
    for(auto object: vm->heap) {
        printf("%p of type: %d\n", object, object->type);
    }

    // 2 is garbage collected
    gc();

    printf("\nAfter VM heap\n");
    for(auto object: vm->heap) {
        printf("%p of type: %d\n", object, object->type);
    }

}


/*
static void test_array_copy_gc() {
    new_array_literal(0, 15);
    new_array_literal(1, 129);
    _copy(0, 1);
    run_gc();
}


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
 * 4 objects created, id(0) and id(1) point to the same object
 * 3 objects should be collected, all but the third created
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
    test_pair_copy_gc();
    //gc_end();
    return 0;
}
