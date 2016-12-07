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

class object {};

class int_object: public object {
public:
    int value;
    unsigned char marked;
};

class char_object: public object {
public:
    char value;
    unsigned char marked;
};

class pair_object: public object {
public:
    struct _object *first;
    struct _object *second;
    unsigned char marked;
};

class Array_Object: public object {
public:
    struct _object **array;
    int array_size;
    unsigned char marked;
};

class String_Object: public object {
public:
    struct _object **string;
    int string_size;
    unsigned char marked;
};

class Struct_Object: public object {
public:
    struct _object **structlist;
    unsigned char marked;
};

class Class_Object: public object {
public:
    struct _object **classlist;
    unsigned char marked;
};

// may keep also a field of when to trigger a GC
#define MAX_STACK_SIZE 4096
#define DEFAULT_HEAP_SIZE 10
class VM {
public:
        map<unsigned long long int, Object*> stack;
        bool garbage_collect;
        unsigned int heap_max;
        vector<Object*> heap;
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
    Object* new_pair_constructor();
    Object* new_array_literal();
    Object* new_string_literal();
    void pushVM(void* stackaddress, Object* object);
    void run_gc();
}

#endif
