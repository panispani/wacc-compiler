#ifndef __GC_H
#define __GC_H

#include "objects.hpp"

#define MAX_STACK_SIZE 4096
#define DEFAULT_HEAP_SIZE 10
class VM {
public:
        map<unsigned long long int, unsigned long long int> stack;
        map<unsigned long long int, object*> heap;
        bool garbage_collect;
        unsigned int heap_max;
        VM();
        ~VM();
};

extern "C" {
    void gc_begin();
    void gc_end();
    uint8_t** new_pair_constructor(object_type type1, object_type type2);
    uint8_t* new_array_literal(int array_size, object_type type);
    object* new_string_literal();
    void pushVM(void* stackaddress, void* heapaddress);
    void run_gc();
}

#endif
