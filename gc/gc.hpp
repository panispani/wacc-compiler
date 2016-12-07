#ifndef __GC_H
#define __GC_H

#include "objects.hpp"

#define MAX_STACK_SIZE 4096
#define DEFAULT_HEAP_SIZE 10
class VM {
public:
        map<unsigned long long int, object*> stack;
        bool garbage_collect;
        unsigned int heap_max;
        vector<object*> heap;
        VM();
        ~VM();
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
