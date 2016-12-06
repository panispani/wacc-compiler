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

//maybe not all of them are needed (primitives)
// do integers even need to be in the VM stack? NO, they
// actually populate it even when we exit scope, TODO
typedef enum {
        INT,
        CHAR,
        PAIR,
        STRING,
        ARRAY,
        STRUCT,
        CLASS
} Object_type;

typedef struct _object {
    union {
        // INT
        int value;

        // CHAR
        char chr;

        // PAIR
        struct {
            struct _object *first;
            struct _object *second;
        };

        // STRING - maybe merge with ARRAY
        struct {
            struct _object **string;
            int string_size;
        };

        // ARRAY
        struct {
            struct _object **array;
            int array_size;
        };

        // STRUCT
        struct {
            struct _object **structlist; // list of pairs, each pair having an element and next pair
        };

        // CLASS
        struct {
            struct _object **classlist; // list of pairs, each pair having an element and next pair
        };

        } fields;

        Object_type type;
        //struct _object *next; // next object in VM stack
        unsigned char marked; // 1 byte, bool is 4 bytes


} Object;

// may keep also a field of when to trigger a GC
//TODO how do i remove variables with scope? I rewrite old ones :)
#define MAX_STACK_SIZE 4096
typedef struct {
        //Object* stack[MAX_STACK_SIZE];
        //int stack_size;
        //list<Object*> stack;
        map<unsigned long long int, Object*> stack;
        // head of object list
        //Object* head;
        // current number of objects
        //int num_objects;
        vector<Object*> heap;
} VM;

extern "C" {
    void gc_begin();
    void gc_end();
    Object* new_pair_constructor();
    Object* new_array_literal();
    Object* new_string_literal();
    void pushVM(void* stackaddress, Object* object);
}

#endif
