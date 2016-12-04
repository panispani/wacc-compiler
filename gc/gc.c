#include <stdlib.h>
#include <string.h>
#include <stdio.h>

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

/************ DATA STRUCTURES *****************/
//maybe not all of them are needed (primitives)
// do integers even need to be in the VM stack? NO, they
// actually populate it even when we exit scope, TODO
typedef enum {
        INT,
        CHAR,
        PAIR,
        STRING,
        ARRAY,
        STRUCT,
        CLASS
} Object_type;

typedef struct _object {
        Object_type type;
        struct _object *next; // next object in VM stack
        unsigned char marked; // 1 byte, bool is 4 bytes

        union {
                // INT
                int value;

                // CHAR
                char chr;

                // PAIR
                struct {
                    struct _object *first;
                    struct _object *second;
                };

                // STRING - maybe merge with ARRAY
                struct {
                    struct _object **string;
                    int string_size;
                };

                // ARRAY
                struct {
                    struct _object **array;
                    int array_size;
                };

                // STRUCT
                struct {
                    struct _object *structlist; // list of pairs, each pair having an element and next pair
                };

                // CLASS
                struct {
                    struct _object *classlist; // list of pairs, each pair having an element and next pair
                };

        } fields;
} Object;

// may keep also a field of when to trigger a GC
// dynamically resizable aaray with realloc - do later
//TODO how do i remove variables with scope? I rewrite old ones :)
#define MAX_STACK_SIZE 4096
typedef struct {
        // array of object pointers
        Object* stack[MAX_STACK_SIZE];
        int stack_size;
        // head of object list
        Object* head;
        // current number of objects
        int num_objects;
} VM;

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
    // primitives are null for now cycle or already done
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


// should think of a clever way to pop struct fields of the stack
#define INTTYPE 1
#define PAIRTYPE 2
#define CHARTYPE 3
#define ARRAYTYPE 4
#define STRUCTTYPE 5

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
Object* declare_pair_constructor(int id, int val1_id, int val2_id) {
        Object* object = new_object();
        object->type = PAIR;
        object->fields.first = get_object_with_id(val1_id);
        object->fields.second = get_object_with_id(val2_id);
        pushVM(object, id);
        return object;
}

Object* declare_pair_copy(int id1, int id2) {
        Object* object = new_object();
        Object* copyfrom = get_object_with_id(id2);
        object->type = PAIR;
        object->fields = copyfrom->fields;
        pushVM(object, id1);
        return object;
}

Object* assign_pair(int id1, int id2) {
        Object* object = get_object_with_id(id2);
        pushVM(object, id1);
        return object;
}

/*** ARRAY ***/
Object* declare_array_literal(int id, int size) {
        Object* object = new_object();
        object->type = ARRAY;
        object->fields.array = (Object**)malloc(size * sizeof(Object*)); // think of using calloc
        memset(object->fields.array, 0, size);
        object->fields.array_size = size;
        pushVM(object, id);
        return object;
}

// refactoring to be done - but NOT now
Object* declare_array_copy(int id1, int id2) {
        Object* object = new_object();
        Object* copyfrom = get_object_with_id(id2);
        object->type = ARRAY;
        object->fields = copyfrom->fields;
        pushVM(object, id1);
        return object;
}

// refactoring to be done - but NOT now
Object* assign_array(int id1, int id2) {
        Object* object = get_object_with_id(id2);
        pushVM(object, id1);
        return object;
}

/*** STRING ***/

/*** STRUCT ***/

/*** CLASS ***/



//called once on startup
void gc_init() {
    vm = newVM();
}

// gc_free is not needed for now
void gc_free() {}

/*** TESTS ***/
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
    Object* obj1 = declare_pair_constructor(0, -1, -1);
    Object* obj2 = declare_pair_constructor(1, -1, -1);
    assign_pair(0, 1);
    run_gc();
}

static void test_array_copy_gc() {
    Object *obj1 = declare_array_literal(0, 15);
    Object *obj2 = declare_array_literal(1, 129);
    assign_array(0, 1);
    run_gc();
}


static void test_complex1_gc() {
    Object* obj1 = declare_pair_constructor(0, -1, -1);
    Object* obj2 = declare_pair_constructor(1, -1, -1);
    Object* obj3 = declare_array_literal(2, 15);
    Object* obj4 = declare_array_literal(3, 129);
    declare_pair_copy(4, 0);
    assign_pair(1, 0);
    run_gc();
}

/************ MAIN *****************/
int main() {
    gc_init();
    test_complex1_gc();
    return 0;
}
