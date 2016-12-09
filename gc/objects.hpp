#ifndef __OBJECTS_H
#define __OBJECTS_H

#include <cstdint>
#include <string>
#include <cstdlib>
#include <iostream>

using namespace std;

// WARNING: These need to match the backend! Check Types.scala
typedef enum {
        ANY,
        INT,
        CHAR,
        BOOL,
        PAIR,
        ARRAY,
        STRUCT,
        PAIR_CONTAINER
} object_type;

// Abstract
class object {
public:
    bool marked;
    uint8_t *bytes;

    virtual ~object() {};
    virtual void mark() = 0;
    virtual string getType() = 0;
    static object* new_obj(int type);
};

// Concrete instances
class pair_container: public object {
public:
    object_type type;
public:
    virtual ~pair_container() {
        //cout << "In pair container destructor" << endl;
    };
    virtual void mark();
    string getType() { return "pointer"; }
};

class pair_object: public object {
public:
    pair_container *first;
    pair_container *second;

    ~pair_object() {
        delete first;
        delete second;
    }
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
