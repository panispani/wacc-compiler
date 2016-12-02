
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
