#ifndef __OBJECTS_H
#define __OBJECTS_H

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <unordered_map>
#include <unordered_set>
#include <list>
#include <vector>
#include <iostream>
#include <stdint.h>
#include <queue>
#include <algorithm>
#include <stack>
#include <set>
#include <string>
#include <map>

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
    virtual void mark() = 0; // should override, pure
    virtual bool isMarked() = 0;
    virtual void setMarked(char m) = 0;
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
    int value;
    unsigned char marked;

    virtual bool isMarked() {
        return marked;
    }

    virtual void setMarked(char m) {
        marked = 1;
    };

    virtual void mark() {}
};

class char_object: public object {
public:
    char value;
    unsigned char marked;

    virtual bool isMarked() {
        return marked;
    }

    virtual void setMarked(char m) {
        marked = 1;
    };

    virtual void mark() {}
};

class bool_object: public object {
public:
    bool value;
    unsigned char marked;

    virtual bool isMarked() {
        return marked;
    }

    virtual void setMarked(char m) {
        marked = 1;
    };

    virtual void mark() {}
};

class pair_object: public object {
public:
    object* first;
    object *second;
    unsigned char marked;

    virtual bool isMarked() {
        return marked;
    }

    virtual void setMarked(char m) {
        marked = 1;
    };

    virtual void mark() {}
};

class array_object: public object {
public:
    int array_size;
    union {
        object **refarray;
        object *staticarray;
    }
    unsigned char marked;

    virtual bool isMarked() {
        return marked;
    }

    virtual void setMarked(char m) {
        marked = 1;
    }

    virtual void mark() {}
};

class struct_object: public object {
public:
    object **structlist;
    unsigned char marked;

    virtual bool isMarked() {
        return marked;
    }

    virtual void setMarked(char m) {
        marked = m;
    };

    virtual void mark() {}
};

class class_object: public object {
public:
    object **classlist;
    unsigned char marked;

    virtual bool isMarked() {
        return marked;
    }

    virtual void setMarked(char m) {
        marked = 1;
    };

    virtual void mark() {}
};


#endif
