// Boolean Variables
bool1 as boolean = true;
bool2 as boolean = false;
bool3 as boolean = true;
bool4 as boolean = false;
bool5 as boolean = true;

// Basic NOT
notBool1 as boolean = not bool1;     // false
notBool2 as boolean = not bool2;     // true

// AND
andTest1 as boolean = bool1 and bool3;       // true
andTest2 as boolean = bool1 and bool2;       // false

// OR
orTest1 as boolean = bool1 or bool2;         // true
orTest2 as boolean = bool2 or bool4;         // false

// NAND
nandTest1 as boolean = bool1 nand bool3;     // false
nandTest2 as boolean = bool1 nand bool2;     // true

// NOR
norTest1 as boolean = bool2 nor bool4;       // true
norTest2 as boolean = bool1 nor bool2;       // false

// XOR
xorTest1 as boolean = bool1 xor bool3;       // false
xorTest2 as boolean = bool1 xor bool2;       // true

// XNOR
xnorTest1 as boolean = bool1 xnor bool3;     // true
xnorTest2 as boolean = bool1 xnor bool2;     // false

// Combined Expressions
combined1 as boolean = (bool1 and bool3) or (bool2 nand bool4);     // true
combined2 as boolean = not(bool2 or bool4) and bool5;               // true
combined3 as boolean =
