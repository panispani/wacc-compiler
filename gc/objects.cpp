#include "objects.hpp"
#include "gc.hpp"
#include "vm.hpp"

object* object::new_obj(int type) {
    switch (static_cast<object_type>(type)) {
        case ANY:
            cout << "Error in garbage collector - attempting to create ANY type" << endl;
            return nullptr;
        case PAIR: return new pair_object();
        case ARRAY: return new array_object();
        case STRUCT: return new struct_object();
        case CLASS: return new class_object();
        case PAIR_CONTAINER: return new pair_container();
        default:
            cout << "Error in garbage collector - attempting to create primitive type" << endl;
            return nullptr;
     }
    return nullptr;
}

void pair_object::mark() {
    //cout << "Marking pair of type (" << firstType << ", " << secondType << ")" << endl;
    marked = 1;
    first->mark();
    second->mark();
}

void array_object::mark() {
    //cout << "Marking array of type " << array_type << "" << endl;
    marked = 1;
    switch (array_type) {
        case ANY: case INT: case CHAR: case BOOL: return;
        default: break;
    }

    uint32_t *bytes = (uint32_t*) this->bytes;
    bytes += 1; // Skip over array size
    for (int i = 0; i < array_size; i++) {
        if (bytes[i] == 0) {
            continue;
        }

        //cout << vm->heap.count(bytes[i]) << endl;
        if (vm->heap.count(bytes[i]) == 0) {
            continue;
        }

        object* meta = vm->heap[bytes[i]];

        //cout << "Marking inside array: " << bytes[i] << " -> " << meta->getType() << endl;
        meta->mark();
    }
}

void struct_object::mark() {
    //cout << "Marking struct" << endl;
    marked = 1;

    uint8_t *bytes = this->bytes;
    for (int i = 0; i < num_types; i++) {
        object_type type = types[i];

        switch (type) {
            case INT: case CHAR: case BOOL: bytes += type_size(type); continue;
            default: break;
        }

        uint32_t *words = (uint32_t *) bytes;
        if (*words == 0) {
            bytes += type_size(type);
            continue;
        }
        object* meta = vm->heap[*words];
        //cout << "Marking inside struct: " << (int) *bytes << " -> " << meta->getType() << endl;
        meta->mark();

        bytes += type_size(type);
    }
}

void class_object::mark() {
    // TODO implement
    marked = 1;
}

void pair_container::mark() {
    marked = 1;
    switch (type) {
        case INT: case ANY: case CHAR: case BOOL: return;
        default: break;
    }
    uint32_t *bytes = (uint32_t*) this->bytes;
    if (bytes[0] == 0) {
        return;
    }
    object* meta = vm->heap[bytes[0]];
    //cout << "Marking inside pointer: " << bytes[0] << " -> " << meta->getType() << endl;
    meta->mark();
}
