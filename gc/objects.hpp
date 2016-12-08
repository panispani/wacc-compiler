#ifndef __OBJECTS_H
#define __OBJECTS_H

#include <cstdint>

typedef enum {
        ANY,
        INT,
        CHAR,
        BOOL,
        PAIR,
        ARRAY,
        STRUCT,
        CLASS,
} object_type;

// Abstract
class object {
public:
    bool marked;
    uint8_t *bytes;

public:
    virtual void mark() = 0;
    static object* new_obj(int type);
};

// Concrete instances
class int_object: public object {
public:
    virtual void mark();
};

class char_object: public object {
public:
    virtual void mark();
};

class bool_object: public object {
public:
    virtual void mark();
};

class pair_object: public object {
public:
    object *first;
    object *second;

public:
    virtual void mark();
};

class array_object: public object {
public:
    object_type array_type;
    int array_size;
    virtual void mark();
};

class struct_object: public object {
public:
    virtual void mark();
};

class class_object: public object {
public:
    virtual void mark();
};


#endif
