#include "gc.hpp"
#include "vm.hpp"

static void test_int_pair_reassignment() {
    gc_begin();
    uint32_t* o1 = new_pair_constructor(INT, INT);
    uint32_t* o2 = new_pair_constructor(INT, INT);
    pushVM(&o1, o1); // o1 = newpair
    pushVM(&o2, o2); // o2 = newpair
    pushVM(&o2, o1); // o2 = o1

    // o1 should be garbage collected
    auto first_pair_address = reinterpret_cast<std::uintptr_t>(o1);
    auto second_pair_address = reinterpret_cast<std::uintptr_t>(o2);
    auto o1_variable_address = reinterpret_cast<std::uintptr_t>(&o1);
    auto o2_variable_address = reinterpret_cast<std::uintptr_t>(&o2);
    bool test_passed = vm->heap.count(first_pair_address) == 1                        // First pair should still exist in the heap
                       && vm->heap.count(second_pair_address) == 0                    // Second pair should be garbage collected
                       && vm->heap.size() == 3                                        // Thus the heap should contain just 3 objects
                       && vm->stack.size() == 2                                       // The stack should still have 2 mappings
                       && vm->stack[o1_variable_address] == first_pair_address        // The first variable should point to the first pair
                       && vm->stack[o2_variable_address] == first_pair_address;       // and so should the second variable

    cout << "PAIR RE-ASSIGNMENT: " << (test_passed ? "PASSED" : "FAILED") << endl;
    gc_end();
}

static void test_pair_pair_reassignment() {
    gc_begin();
    uint32_t *o1 = new_pair_constructor(PAIR, PAIR);
    uint32_t *o2 = new_pair_constructor(PAIR, PAIR);
    uint32_t *p1 = new_pair_constructor(INT, INT);
    uint32_t *p2 = new_pair_constructor(INT, INT);
    uint32_t *p3 = new_pair_constructor(INT, INT);
    uint32_t *p4 = new_pair_constructor(INT, INT);

    auto first_entry_pair_address = reinterpret_cast<std::uintptr_t>(o1);
    auto second_entry_pair_address = reinterpret_cast<std::uintptr_t>(o2);

    auto first_pair_address = reinterpret_cast<std::uintptr_t>(p1);
    auto second_pair_address = reinterpret_cast<std::uintptr_t>(p2);
    auto third_pair_address = reinterpret_cast<std::uintptr_t>(p3);
    auto fourth_pair_address = reinterpret_cast<std::uintptr_t>(p4);

    auto o1_variable_address = reinterpret_cast<std::uintptr_t>(&o1);
    auto o2_variable_address = reinterpret_cast<std::uintptr_t>(&o2);

    pair_object *meta = (pair_object *) vm->heap[first_entry_pair_address];
    memcpy(meta->first->bytes, &first_pair_address, 4);
    memcpy(meta->second->bytes, &second_pair_address, 4);

    meta = (pair_object *) vm->heap[second_entry_pair_address];
    memcpy(meta->first->bytes, &third_pair_address, 4);
    memcpy(meta->second->bytes, &fourth_pair_address, 4);

    pushVM(&o1, o1); // o1 = newpair(newpair(1, 1), newpair(2, 2))
    pushVM(&o2, o2); // o2 = newpair(newpair(3, 3), newpair(4, 4))
    pushVM(&o2, o1); // o2 = o1

    // o2 should be garbage collected, and so should the pairs contained within it
    bool test_passed = vm->heap.count(first_entry_pair_address) == 1                        // First pair should still exist in the heap
                       && vm->heap.count(second_entry_pair_address) == 0                    // Second pair should be garbage collected
                       && vm->heap.count(first_pair_address) == 1
                       && vm->heap.count(second_pair_address) == 1
                       && vm->heap.count(third_pair_address) == 0
                       && vm->heap.count(fourth_pair_address) == 0
                       && vm->heap.size() == 9                                              // Thus the heap should contain 9 objects (3 pairs)
                       && vm->stack.size() == 2                                             // The stack should still have 2 mappings
                       && vm->stack[o1_variable_address] == first_entry_pair_address        // The first variable should point to the first array
                       && vm->stack[o2_variable_address] == first_entry_pair_address;       // and so should the second array

    cout << "PAIR OF PAIRS RE-ASSIGNMENT: " << (test_passed ? "PASSED" : "FAILED") << endl;
    gc_end();
}

static void test_multidimensional_array_reassignment() {
    gc_begin();
    uint32_t* o1 = (uint32_t*) new_array_literal(3, INT);
    uint32_t* o2 = (uint32_t*) new_array_literal(3, INT);
    uint32_t* o3 = (uint32_t*) new_array_literal(2, ARRAY);
    o3[0] = 2;
    o3[1] = reinterpret_cast<std::uintptr_t>(o1);
    o3[2] = reinterpret_cast<std::uintptr_t>(o2);
    pushVM(&o3, o3); // declare o3 array

    uint32_t* o4 = (uint32_t*) new_array_literal(3, INT);
    uint32_t* o5 = (uint32_t*) new_array_literal(3, INT);
    uint32_t* o6 = (uint32_t*) new_array_literal(2, ARRAY);
    o6[0] = 2;
    o6[1] = reinterpret_cast<std::uintptr_t>(o4);
    o6[2] = reinterpret_cast<std::uintptr_t>(o5);
    pushVM(&o6, o6); // declare o6 array
    pushVM(&o6, o3); // o6 = o3

    collect_garbage();

    auto third_array_address = reinterpret_cast<std::uintptr_t>(o3);
    auto fourth_array_address = reinterpret_cast<std::uintptr_t>(o4);
    auto fifth_array_address = reinterpret_cast<std::uintptr_t>(o5);
    auto six_array_address = reinterpret_cast<std::uintptr_t>(o6);
    auto o3_variable_address = reinterpret_cast<std::uintptr_t>(&o3);
    auto o6_variable_address = reinterpret_cast<std::uintptr_t>(&o6);

    //o4, o5, o6 should be collected

    bool test_passed = vm->heap.count(fourth_array_address) == 0                       // Fourth array should be garbage collected
                       && vm->heap.count(fifth_array_address) == 0                     // Fifth array should be garbage collected
                       && vm->heap.count(six_array_address) == 0                       // Sixth array should be garbage collected
                       && vm->heap.size() == 3                                         // Thus the heap should contain 3 objects
                       && vm->stack.size() == 2                                        // The stack should have 3 mappings
                       && vm->stack[o3_variable_address] == third_array_address        // The first variable should point to the first array
                       && vm->stack[o6_variable_address] == third_array_address;       // and so should the second array
    cout << "MULTIDIMENSIONAL ARRAY RE-ASSIGNMENT: " << (test_passed ? "PASSED" : "FAILED") << endl;
    gc_end();
}

static void test_int_array_reassignment() {
    gc_begin();
    uint8_t* o1 = new_array_literal(3, INT);
    uint8_t* o2 = new_array_literal(3, INT);
    pushVM(&o1, o1); // o1 = [1, 2, 3]
    pushVM(&o2, o2); // o2 = [4, 5, 6]
    pushVM(&o2, o1); // o2 = o1
    collect_garbage(); // Have to force it as only 2 heap items are allocated

    // o1 should be garbage collected
    auto first_array_address = reinterpret_cast<std::uintptr_t>(o1);
    auto second_array_address = reinterpret_cast<std::uintptr_t>(o2);
    auto o1_variable_address = reinterpret_cast<std::uintptr_t>(&o1);
    auto o2_variable_address = reinterpret_cast<std::uintptr_t>(&o2);
    bool test_passed = vm->heap.count(first_array_address) == 1                        // First array should still exist in the heap
                       && vm->heap.count(second_array_address) == 0                    // Second array should be garbage collected
                       && vm->heap.size() == 1                                         // Thus the heap should contain just 1 object
                       && vm->stack.size() == 2                                        // The stack should still have 2 mappings
                       && vm->stack[o1_variable_address] == first_array_address        // The first variable should point to the first array
                       && vm->stack[o2_variable_address] == first_array_address;       // and so should the second array
    cout << "INT ARRAY RE-ASSIGNMENT: " << (test_passed ? "PASSED" : "FAILED") << endl;
    gc_end();
}

static void test_pair_array_reassignment() {
    gc_begin();
    uint32_t* o1 = (uint32_t*) new_array_literal(2, PAIR);
    uint32_t* o2 = (uint32_t*) new_array_literal(2, PAIR);
    uint32_t* p1 = new_pair_constructor(INT, INT);
    uint32_t* p2 = new_pair_constructor(INT, INT);
    uint32_t* p3 = new_pair_constructor(INT, INT);
    uint32_t* p4 = new_pair_constructor(INT, INT);

    auto first_array_address = reinterpret_cast<std::uintptr_t>(o1);
    auto second_array_address = reinterpret_cast<std::uintptr_t>(o2);

    auto first_pair_address = reinterpret_cast<std::uintptr_t>(p1);
    auto second_pair_address = reinterpret_cast<std::uintptr_t>(p2);
    auto third_pair_address = reinterpret_cast<std::uintptr_t>(p3);
    auto fourth_pair_address = reinterpret_cast<std::uintptr_t>(p4);

    auto o1_variable_address = reinterpret_cast<std::uintptr_t>(&o1);
    auto o2_variable_address = reinterpret_cast<std::uintptr_t>(&o2);

    o1[0] = 2;
    o1[1] = first_pair_address;
    o1[2] = second_pair_address;

    o2[0] = 2;
    o2[1] = third_pair_address;
    o2[2] = fourth_pair_address;

    pushVM(&o1, o1); // o1 = [newpair(1, 1), newpair(2, 2)]
    pushVM(&o2, o2); // o2 = [newpair(3, 3), newpair(4, 4)]
    pushVM(&o2, o1); // o2 = o1

    // o2 should be garbage collected, and so should the pairs contained within it
    bool test_passed = vm->heap.count(first_array_address) == 1                        // First array should still exist in the heap
                       && vm->heap.count(second_array_address) == 0                    // Second array should be garbage collected
                       && vm->heap.count(first_pair_address) == 1
                       && vm->heap.count(second_pair_address) == 1
                       && vm->heap.count(third_pair_address) == 0
                       && vm->heap.count(fourth_pair_address) == 0
                       && vm->heap.size() == 7                                         // Thus the heap should contain 7 objects (2 pairs and an array)
                       && vm->stack.size() == 2                                        // The stack should still have 2 mappings
                       && vm->stack[o1_variable_address] == first_array_address        // The first variable should point to the first array
                       && vm->stack[o2_variable_address] == first_array_address;       // and so should the second array
    cout << "PAIR ARRAY RE-ASSIGNMENT: " << (test_passed ? "PASSED" : "FAILED") << endl;
    gc_end();
}

static void test_struct_reassignment() {
    gc_begin();
    object_type types[] = { INT, INT, PAIR };
    uint32_t *o1 = (uint32_t*) new_struct_literal(12, 3, types);
    uint32_t *o2 = (uint32_t*) new_struct_literal(12, 3, types);
    uint32_t *p1 = new_pair_constructor(INT, INT);
    uint32_t *p2 = new_pair_constructor(INT, INT);

    auto first_literal_address = reinterpret_cast<std::uintptr_t>(o1);
    auto second_literal_address = reinterpret_cast<std::uintptr_t>(o2);

    auto first_pair_address = reinterpret_cast<std::uintptr_t>(p1);
    auto second_pair_address = reinterpret_cast<std::uintptr_t>(p2);

    auto o1_variable_address = reinterpret_cast<std::uintptr_t>(&o1);
    auto o2_variable_address = reinterpret_cast<std::uintptr_t>(&o2);

    cout << "Struct 1 bytes address in test case: " << first_literal_address << endl;
    o1[0] = 1;
    o1[1] = 1;
    o1[2] = first_pair_address;

    cout << "Struct 2 bytes address in test case: " << second_literal_address << endl;
    o2[0] = 2;
    o2[1] = 2;
    o2[2] = second_pair_address;

    pushVM(&o1, o1); // o1 = {1, 1, newpair(1, 1)}
    pushVM(&o2, o2); // o2 = {2, 2, newpair(2, 2)}
    pushVM(&o2, o1); // o2 = o1

    // o2 should be garbage collected, and so should the pair contained within it
    bool test_passed = vm->heap.count(first_literal_address) == 1                        // First literal should still exist in the heap
                       && vm->heap.count(second_literal_address) == 0                    // Second literal should be garbage collected
                       && vm->heap.count(first_pair_address) == 1
                       && vm->heap.count(second_pair_address) == 0
                       && vm->heap.size() == 4                                           // Thus the heap should contain 3 objects (struct and pair)
                       && vm->stack.size() == 2                                          // The stack should still have 2 mappings
                       && vm->stack[o1_variable_address] == first_literal_address        // The first variable should point to the first array
                       && vm->stack[o2_variable_address] == first_literal_address;       // and so should the second array

    cout << "STRUCT RE-ASSIGNMENT: " << (test_passed ? "PASSED" : "FAILED") << endl;
    gc_end();
}

/************ MAIN *****************/
int main() {
    test_int_pair_reassignment();
    test_int_array_reassignment();
    test_pair_array_reassignment();
    test_pair_pair_reassignment();
    test_multidimensional_array_reassignment();
    test_struct_reassignment();
    return 0;
}
