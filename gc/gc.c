#include <stdlib.h>

// in use means referenced by a variable in scope
// or the object referenced by another object that is in use
// make a graph of reachable objects
// mark and sweep
// traverse entire object graph, mark each one as reachable
// traverse heap and delete objects that are unreachable

/************ DATA STRUCTURES *****************/
//maybe not all of them are needed e.g. array
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
	unsigned char marked; // 1 byte, bool is 4 bytes

	union fields {
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

	};
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
	  // add dummy head to stack? YES we now get null pointer excpection
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
	object->type = type;
	object->marked = 0;
	// append to front of VM object list
	object->next = vm->head->next;
	vm->head = object;
	vm->num_objects = vm->num_objects + 1;
	return object;
}


// only call this function
void* gc_malloc(int type, int stackbytes, int refbytes) {
	VM* vm = newVM(); // TODO make static
	Object *object = new_object(vm, type, stackbytes, refbytes);
	return (void*)object;
	// pop from vm stack the refbytes in object list
}

// gc_free is not needed for now
void gc_free() {}

int main() {
    Object* object = gc_malloc(0, 4, 0);
    return 0;
}
