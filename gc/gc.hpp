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
    uint32_t* new_pair_constructor(object_type type1, object_type type2);
    uint8_t* new_array_literal(int array_size, object_type type);
    object* new_string_literal();
    void pushVM(void* stackaddress, void* heapaddress);
    void collect_garbage();
    int type_size(object_type);
}

#endif
