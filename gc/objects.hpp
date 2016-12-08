#ifndef __OBJECTS_H
#define __OBJECTS_H

#include <cstdint>
#include <string>

using namespace std;

typedef enum {
        ANY,
        INT,
        CHAR,
        BOOL,
        PAIR,
        ARRAY,
        STRUCT,
        CLASS,
        POINTER
} object_type;

// Abstract
class object {
public:
    bool marked;
    uint8_t *bytes;

public:
    virtual void mark() = 0;
    virtual string getType() = 0;
    static object* new_obj(int type);
};

// Concrete instances
class pointer_object: public object {
public:
    object_type type;
public:
    virtual void mark();
    string getType() { return "pointer"; }
};

class int_object: public object {
public:
    virtual void mark();
    string getType() { return "int"; }
};

class char_object: public object {
public:
    virtual void mark();
    string getType() { return "char"; }
};

class bool_object: public object {
public:
    virtual void mark();
    string getType() { return "bool"; }
};

class pair_object: public object {
public:
    pointer_object *first;
    pointer_object *second;
    object_type firstType;
    object_type secondType;

public:
    virtual void mark();
    string getType() { return "pair"; }
};

class array_object: public object {
public:
    object_type array_type;
    int array_size;
    virtual void mark();
    string getType() { return "array"; }
};

class struct_object: public object {
public:
    int num_types;
    object_type *types;
    virtual void mark();
    string getType() { return "struct"; }
};

class class_object: public object {
public:
    virtual void mark();
    string getType() { return "class"; }
};


#endif
