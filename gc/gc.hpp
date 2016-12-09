#ifndef __GC_H
#define __GC_H

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <iostream>
#include <algorithm>
#include <vector>
#include <string>
#include <cstdint>
#include "objects.hpp"

using namespace std;

extern "C" {
    void gc_begin();
    void gc_end();
    unsigned long long int gc_free(void* obj);
    uint32_t* new_pair(object_type type1, object_type type2);
    uint8_t* new_array_literal(int array_size, object_type type);
    uint8_t* new_struct_literal(int struct_size, int num_types, object_type *types);
    void push_stack(void* stackaddress, void* heapaddress);
    void remove_stack(void* obj);
    void remove_function_stack(void* fp);
    void collect_garbage();
    int type_size(object_type);
}

#endif
