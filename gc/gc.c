
// in use means referenced by a variable in scope
// or the object referenced by another object that is in use
// make a graph of reachable objects
// mark and sweep
// traverse entire object graph, mark each one as reachable
// traverse heap and delete objects that are unreachable

Object *newObject(VM* vm, int type, int stackbytes, int refbytes) {
	return NULL;
}
	
gc_init() {
	vm = newVM();
}

//todo
VM* newVM() {
	  VM* vm = malloc(sizeof(VM));
	  return vm;
}

void gc_malloc(int type, int stackbytes, int refbytes) {
	static VM vm = newVM();
	Object *object = newObject(vm, type, stackbytes, refbytes);
	// pop from vm stack the refbytes in object list
	// push(vm, object);
}

void gc() {
	
}

void gc_free() {
	// gc_free is not needed for now
}
