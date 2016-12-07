#ifndef __OBJECTS_H
#define __OBJECTS_H

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <iostream>
//#include <stdint.h>
#include <algorithm>
#include <vector>
#include <string>
#include <map>
#include <cstdint>

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
} object_type;


// abstract
class object {
public:
    bool marked;

public:
    virtual void mark() = 0;
    static object* new_obj(int type);
};

/*
 * WARNING:
 * duplication is introduced on purpose
 * This is because of the way we implemented the ARM backend
 * and the assumptions did there about memory
 */

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
