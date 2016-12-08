#include "objects.hpp"
#include "gc.hpp"
#include "vm.hpp"

object* object::new_obj(int type) {
    switch (static_cast<object_type>(type)) {
        case ANY:
            cout << "not implemented" << endl;
            return nullptr; // not done for now
        case INT: return new int_object();
        case CHAR: return new char_object();
        case BOOL: return new bool_object();
        case PAIR: return new pair_object();
        case ARRAY: return new array_object();
        case STRUCT: return new struct_object();
        case CLASS: return new class_object();
        case POINTER: return new pointer_object();
    }
    return nullptr;
}

void int_object::mark() {
    cout << "Marking int" << endl;
    marked = 1;
}

void char_object::mark() {
    marked = 1;
}

void bool_object::mark() {
    marked = 1;
}

void pair_object::mark() {
    cout << "Marking pair of type (" << firstType << ", " << secondType << ")" << endl;
    marked = 1;
    cout << "First: " << first << endl;
    cout << "Second: " << second << endl;
    first->mark();
    second->mark();
}

void array_object::mark() {
    cout << "Marking array of type " << array_type << "" << endl;
    marked = 1;
    switch (array_type) {
        case INT: case CHAR: case BOOL: return;
        default: break;
    }

    uint32_t *bytes = (uint32_t*) this->bytes;
    cout << "Array size: " << bytes[0] << endl;
    bytes += 1; // Skip over array size
    for (int i = 0; i < array_size; i++) {
        object* meta = vm->heap[bytes[i]];
        cout << "Marking inside array: " << bytes[i] << " -> " << meta->getType() << endl;
        meta->mark();
    }
}

void struct_object::mark() {
    marked = 1;
}

void class_object::mark() {
    marked = 1;
}

void pointer_object::mark() {
    marked = 1;
    switch (type) {
        case INT: case CHAR: case BOOL: return;
        default: break;
    }
    uint32_t *bytes = (uint32_t*) this->bytes;
    //cout << "Pointer bytes address: " << (int) bytes << endl;
    cout << "Contents of pointer: " << (int) bytes[0] << endl;
    object* meta = vm->heap[bytes[0]];
    cout << "Marking inside pointer: " << bytes[0] << " -> " << meta->getType() << endl;
    meta->mark();
}
