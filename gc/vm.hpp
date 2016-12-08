#ifndef GC_VM_HPP
#define GC_VM_HPP

#include <map>
#include "objects.hpp"

using namespace std;

#define MAX_STACK_SIZE 4096
#define DEFAULT_HEAP_SIZE 10

class VM {
public:
    //TODO change these to uint8_t* or const uint8_t*
    map<unsigned long long int, unsigned long long int> stack;
    map<unsigned long long int, object*> heap;
    bool garbage_collect;
    unsigned int heap_max;
    VM();
    ~VM();
};

extern VM *vm;

#endif
