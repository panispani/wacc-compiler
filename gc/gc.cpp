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

/************ STATIC FUNCTIONS *****************/

static VM* vm;

static void mark(object* obj) {
    // printf("marking: %p\n", obj);
    if (obj == NULL) {
        return;
    }
    obj.mark();
}
    /*
    obj->marked = 1;
    switch(obj->type) {
    case PAIR:
        mark(obj->fields.first);
        mark(obj->fields.second);
        break;
    }
    /*case ARRAY:
        ;int i = 0;
        for (; i < obj->fields.array_size; i++) {
            mark(obj->fields.array[i]);
        }
        break;
    }*/
    // TODO REST

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
    vector<object*> to_delete;
    to_delete.clear();
    for (auto obj: vm->heap) {
        if (!obj->marked) {
            to_delete.push_back(obj);
        } else {
            obj->marked = 0;
        }
    }
    // we should update heap and free to_delete elements
    vector<object*> newheap;
    set_difference(vm->heap.begin(), vm->heap.end(),
            to_delete.begin(), to_delete.end(),
            inserter(newheap, newheap.end()));
    swap(vm->heap, newheap);
    for (auto obj: to_delete) {
        free(obj);
    }
}


/*
  unsigned int current = 0;
  object** obj = &vm->heap[current];
  while (current != vm->heap.size()) {
    if (!(*obj)->marked) {
      object* unreached = *obj;
      *obj = unreached->next;
      free(unreached);
    } else {
      // obj was reached by marking, reset it for next GC
      (*obj)->marked = 0;
      obj = &(*obj)->next;
    }
  }
  */


static void gc() {
    markAll();
    sweep();
}

// heuristics of when to call GC
static bool should_gc() {
    cout << "vm heap " << vm->heap.size() << endl;
    cout << "vm max heap " << vm->heap_max / 2 << endl;
    return vm->heap.size() >= vm->heap_max / 2;
}

static object* new_obj() {
        if (should_gc()) {
            vm->garbage_collect = true;
            //cout << "I garbage collected" << endl;
            // gc();
        }
        object* obj = (object*)malloc(sizeof(object));
        if (obj == NULL) {
            printf("%s\n", "Stack overflow");
            return obj;
        }

        obj->marked = 0;

        vm->heap.push_back(obj);
        return obj;
}

/************ PUBLIC FUNCTIONS *****************/

// call on declaration and assignment
void pushVM(void* stackaddress, object* obj) {
    auto addr = reinterpret_cast<std::uintptr_t>(stackaddress);
    vm->stack[addr] = obj;
    if (vm->garbage_collect) {
        vm->garbage_collect = false;
        run_gc();
    }
}

/*** PAIR ***/
object* new_pair_constructor() {
    object* obj = new_obj();
    obj->type = PAIR;
    obj->fields.first = new_obj();
    obj->fields.second = new_obj();
    return obj;
}


/*** ARRAY ***/
// NOTE: dont refactor with string yet
object* new_array_literal(int array_size) {
        object* obj = new_obj();
        obj->type = ARRAY;
        // think of using calloc
        obj->fields.array = (object**)malloc(array_size * sizeof(object*));
        //memset(obj->fields.array, 0, size);
        obj->fields.array_size = array_size;
        //pushVM(obj, id);
        return obj;
}


/*** STRING ***/
object* new_string_literal(int id, int size) {
        object* obj = new_obj();
        obj->type = STRING;
        // think of using calloc
        obj->fields.string = (object**)malloc(size * sizeof(object*));
        memset(obj->fields.string, 0, size);
        obj->fields.string_size = size;
        //pushVM(obj, id);
        return obj;
}

/*** STRUCT ***/

/*** CLASS ***/

/*** COMMON METHODS ***/
// form: foo(dst, src)
// this is ugly and will change in the process of refactoring
/*object* _copy(int id1, int id2) {
    object* obj = get_obj_with_id(id2);
    //pushVM(obj);
    return obj;
}*/

/* Dont delete yet, creating a new obj is wrong
 * delcaring in on the stack, there is no new
 * variable on the heap
object* declare_copy(int id1, int id2) {
        object* obj = new_obj();
        object* copyfrom = get_obj_with_id(id2);
        obj->type = copyfrom->type;
        obj->fields = copyfrom->fields;
        return obj;
}
*/

//might not be used if global - to try TODO
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
        printf("%p of type: %d\n", obj, obj->type);
    }

    gc();

    printf("\nAfter VM heap\n");
    for(auto obj: vm->heap) {
        printf("%p of type: %d\n", obj, obj->type);
    }

}


static void test_pair_copy_gc() {
    object* o1 = new_pair_constructor();
    object* o2 = new_pair_constructor();
    pushVM(&o1, o1);
    pushVM(&o2, o2);
    auto addr = reinterpret_cast<std::uintptr_t>(&o1);
    vm->stack[addr] = o2; //o1 should be collected
    run_gc();
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
/*int main() {
    gc_begin();
    test_pair_copy_gc();
    gc_end();
    return 0;
}*/
