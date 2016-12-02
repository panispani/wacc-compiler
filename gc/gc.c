
// in use means referenced by a variable in scope
// or the object referenced by another object that is in use
// make a graph of reachable objects
// mark and sweep
// traverse entire object graph, mark each one as reachable
// traverse heap and delete objects that are unreachable

//maybe not all of them are needed e.g. array
typedef enum {
	INT,
	CHAR,
	PAIR,
	STRING,
	ARRAY,
	STRUCT,
	CLASS
} object_type;

typedef struct _object {
	object_type type;
	unsigned char marked; // 1 byte, bool is 4 bytes
	
	union fields {
		// INT
		int value;
		
		// CHAR
		char value;
		
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
			struct _object *list; // list of pairs, each pair having an element and next pair
		};
		
		// CLASS
		struct {
			struct _object *list; // list of pairs, each pair having an element and next pair
		};
		
	};
} object;

static Object *newObject(VM* vm, int type, int stackbytes, int refbytes) {
	return NULL;
}
	
//todo
static VM* newVM() {
	  VM* vm = malloc(sizeof(VM));
	  return vm;
}


static void gc() {
	
}

// only call this function
void gc_malloc(int type, int stackbytes, int refbytes) {
	static VM vm = newVM();
	Object *object = newObject(vm, type, stackbytes, refbytes);
	// pop from vm stack the refbytes in object list
	// push(vm, object);
}

// gc_free is not needed for now
void gc_free() {}
