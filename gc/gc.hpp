#ifndef __GC_H
#define __GC_H

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
        INT,
        CHAR,
        PAIR,
        ARRAY,
        STRING,
        STRUCT,
        CLASS
} object_type;


// abstract
class object {
public:
    //virtual object() = 0; // pure
    virtual void mark() = 0; // should override
    virtual bool isMarked() = 0;
    virtual void setMarked(char m) = 0;
    static object* new_obj(int type) {
        // todo - factory
        return nullptr;
    }
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
    object **array;
    int array_size;
    unsigned char marked;

    virtual bool isMarked() {
        return marked;
    }

    virtual void setMarked(char m) {
        marked = 1;
    }

    virtual void mark() {}
};

class string_object: public object {
public:
    object **string;
    int string_size;
    unsigned char marked;

    virtual bool isMarked() {
        return marked;
    }

    virtual void setMarked(char m) {
        marked = 1;
    };

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

// may keep also a field of when to trigger a GC
#define MAX_STACK_SIZE 4096
#define DEFAULT_HEAP_SIZE 10
class VM {
public:
        map<unsigned long long int, object*> stack;
        bool garbage_collect;
        unsigned int heap_max;
        vector<object*> heap;
        VM() {
            heap.clear();
            garbage_collect = false;
            heap_max = DEFAULT_HEAP_SIZE;
            stack.clear();
        }
        ~VM() {
            for (auto object: heap) {
                free(object);
            }
        }
};

extern "C" {
    void gc_begin();
    void gc_end();
    object* new_pair_constructor();
    object* new_array_literal();
    object* new_string_literal();
    void pushVM(void* stackaddress, object* object);
    void run_gc();
}

#endif
