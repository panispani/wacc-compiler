#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include "gc.h"

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
          vm->num_objects = 0;
          vm->stack_size = 0;
          vm->head = NULL;
          return vm;
}

static void mark(Object* object) {
    // primitives are null for now
    // cycle or already done
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
    case ARRAY:
        ;int i = 0;
        for (; i < object->fields.array_size; i++) {
            mark(object->fields.array[i]);
        }
        break;
    }
    // TODO REST
}

static void markAll() {
    // start marking from the stack allocated variables
    int i;
    for (i = 0; i < vm->stack_size; i++) {
        //printf("in %p %d\n", vm->stack[i], vm->stack[i]->type);
        mark(vm->stack[i]);
        //printf("out %p\n", vm->stack[i]);
    }
}


// we use pointer to pointer so we can change
// the list(remove element) and it retains its structure!
void sweep() {
  Object** object = &vm->head;
  while (*object != NULL) {
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
}


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
        // rethink
        Object* object = (Object*)malloc(sizeof(Object));
        if (object == NULL) {
            printf("%s\n", "Stack overflow");
            return object;
        }

        object->marked = 0;
        // append to front of VM object list
        if (vm->head == NULL) {
            object->next = NULL;
        } else {
            object->next = vm->head;
        }
        vm->head = object;
        vm->num_objects = vm->num_objects + 1;

        return object;
}

// put on stack of VM
static void pushVM(Object* object, int id) {
        // take in account stack re-pushing
        if (vm->stack[id] == NULL) {
            vm->stack_size++;
        }
        vm->stack[id] = object;
}

// look on stack for object with corresponing id
static Object* get_object_with_id(int id) {
        if (id == -1) {
            // not a reference type, shouldn't care
            return NULL; // or int-singleton
        }
        return vm->stack[id];
}


/************ PUBLIC FUNCTIONS *****************/
/*** PAIR ***/
// how about pair literal? is it even needed?
Object* new_pair_constructor(int id, int val1_id, int val2_id) {
    // new object should be called
    // as we create a new object
    // what we should look out for is not creating a new variable
    // on the stack, currently in pushVM this makes no difference
    // but keep a note about it
    Object* object = new_object();
    object->type = PAIR;
    object->fields.first = get_object_with_id(val1_id);
    object->fields.second = get_object_with_id(val2_id);
    pushVM(object, id);
    return object;
}


/*** ARRAY ***/
// declare and assign
// NOTE: dont refactor with string yet
Object* new_array_literal(int id, int size) {
        Object* object = new_object();
        object->type = ARRAY;
        // think of using calloc
        object->fields.array = (Object**)malloc(size * sizeof(Object*));
        memset(object->fields.array, 0, size);
        object->fields.array_size = size;
        pushVM(object, id);
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
        pushVM(object, id);
        return object;
}

/*** STRUCT ***/

/*** CLASS ***/

/*** COMMON METHODS ***/
// form: foo(dst, src)
// this is ugly and will change in the process of refactoring
Object* _copy(int id1, int id2) {
    Object* object = get_object_with_id(id2);
    pushVM(object, id1);
    return object;
}

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
    vm->stack_size = 0;
    gc();
    free(vm);
}

/********************* TESTS *************************/
static void run_gc() {

    // subject to change
    printf("INTTYPE 1\nPAIRTYPE 2\nCHARTYPE 3\nARRAYTYPE 4\nSTRUCTTYPE 5\n");

    printf("\nBefore VM heap\n");
    Object* p = vm->head;
    while(p != NULL) {
        printf("%p of type: %d\n", p, p->type);
        p = p->next;
    }

    // 2 is garbage collected
    gc();

    printf("\nAfter VM heap\n");
    p = vm->head;
    while(p != NULL) {
        printf("%p of type: %d\n", p, p->type);
        p = p->next;
    }
}

static void test_pair_copy_gc() {
    new_pair_constructor(0, -1, -1);
    new_pair_constructor(1, -1, -1);
    _copy(0, 1);
    run_gc();
}

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

/*
 * 4 objects created, id(0) and id(1) point to the same object
 * 3 objects should be collected, all but the third created
 */
static void test_complex2_gc() {
    new_array_literal(0, 2);
    new_array_literal(1, 2938);
    new_array_literal(0, 10);
    new_array_literal(1, 20);
    _copy(1, 0);
    run_gc();
}

/************ MAIN *****************/
int main() {
    gc_begin();
    test_pair_copy_gc();
    gc_end();
    return 0;
}
