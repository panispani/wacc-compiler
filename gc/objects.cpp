#include "objects.hpp"

object* object::new_obj(int type) {
    switch (static_cast<object_type>(type)) {
        case ANY:
        cout << "not implemented" << endl;
        return nullptr; // not done for now
        break;
    case INT:
        return new int_object();
        break;
    case CHAR:
        return new char_object();
        break;
    case BOOL:
        return new bool_object();
        break;
    case PAIR:
        return new pair_object();
        break;
    case ARRAY:
        return new array_object();
        break;
    case STRUCT:
        return new struct_object();
        break;
    case CLASS:
        return new class_object();
        break;
    }
    return nullptr;
}
