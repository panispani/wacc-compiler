#include <stdlib.h>
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
 * /

/************ DATA STRUCTURES *****************/
//maybe not all of them are needed e.g. array
// do integers even need to be in the VM stack?
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

		// STRING
		struct {
			struct _object *ch;    // char, last one is '\0'
			struct _object *next; // a pair
		};

		// ARRAY - will think about it

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
// also maybe add a stack maximum? i think not needed
typedef struct {
	// list of object pointers, until malloc fails
	Object** stack;
	// head of object list
	Object* head;
	// current number of objects
	int num_objects;
} VM;


/************ FUNCTIONS *****************/

static VM* newVM() {
	  VM* vm = malloc(sizeof(VM));
	  vm->num_objects = 0;
	  vm->head = NULL;
	  return vm;
}

static void gc() {

}

// heuristics of when to call GC
static int should_gc() {
	// casted to false
	return 0;
}

static Object* new_object(VM* vm, int type, int stackbytes, int refbytes) {
	if (should_gc()) {
		gc();
	}
	// rethink
	Object* object = malloc(stackbytes + refbytes);
	if (object == NULL) {
		printf("%s\n", "Stack overflow");
		return object;
	}
	object->type = type;
	object->marked = 0;
	// append to front of VM object list
	if (vm->head == NULL) {
      object->next = NULL;
	} else {
      object->next = vm->head->next;
	}
	vm->head = object;
	vm->num_objects = vm->num_objects + 1;
	
	//initialise depending on type to default
	
	
	return object;
}

// should think of a clever way to pop struct fields of the stack
// called every time you declare a pair
#define INTTYPE 1
#define PAIRTYPE 2
#define CHARTYPE 3
#define ARRAYTYPE 4
#define STRUCTTYPE 5

void declare_pair() {
	Object* object = new_object(vm, PAIRTYPE, sizeof(size_t)); // pointer size
}
// called every time you assign an integer	
void assign_pair() {
	
}


// separate functions for each type







static VM* vm;
//called once on startup
void gc_init() {
    vm = newVM();
}

// only call this function - this is only true for struct types..
void* gc_malloc(int type, int stackbytes, int refbytes) {
	Object *object = new_object(vm, type, stackbytes, refbytes);
	return (void*)object;
	// pop from vm stack the refbytes in object list
}

// gc_free is not needed for now
void gc_free() {}

int main() {
    gc_init();
    Object* object = gc_malloc(42, 4, 0);
    printf("%d\n", vm->head->type);
    return 0;
}
