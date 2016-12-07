#include "objects.hpp"
#include "gc.hpp"

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
    cout << "Marking pair" << endl;
    marked = 1;
    first->mark();
    second->mark();
}

void array_object::mark() {
    marked = 1;
}

void struct_object::mark() {
    marked = 1;
}

void class_object::mark() {
    marked = 1;
}
