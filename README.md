# Porado

Porado is a simple programming language that compiles to native assembly.

## Variables

Variables in Porado have strict static typing. Variable declaration requires developers to specify the data type and variable identifier in the following format:

```java
var <identifier> as <data-type>;
```

Variables can be instantiated at declaration, or later in the code by calling its identifier:

```java
var myVariable1 as int = 12345;
var myVariable2 as float;
myVariable2 = 123.45;
```

An uninitialized variable will use its default value when its identifier is called. More details at the **Data Types** section.

### Fixed Variables

Variables can be set to immutable and read-only by adding the `fixed` keyword before its data type. A fixed variable will only keep the value it was instantiated with; you can declare a fixed variable without an initial value then assign it only once afterwards. If you try to assign a new value to a fixed variable, it will trigge a compile-time error.

```java
var PI as fixed float = 3.14159265359;
PI = 3; // Compile-time error

var goldenRatio as fixed float;
goldenRatio = 1.61803398875;
goldenRatio = 123; // Compile-time error
```

### Arrays

An array can hold multiple variables of the same type. You can access a specific element of an array using its array index, which start at 0 to represent the first element. To declare an array, you add the `array` keyword after the data type. An array in Porado uses the square braces `[ ]` to declare its elements. You can't access elements outside the array's length:

```java
var myFloatArray as float array = [1.2, 3.4, 5.6];

// Takes the element at index 0 and assigns it to myFloat.
var myFloat as float = myFloatArray[0];

// Assigns a new value to element at index 1
myFloatArray[1] = 7.8;

// Will throw an error.
myFloatArray[100] = 100.0;
```

To declare an empty array without specifying its elements, you add the `of` keyword after the array keyword then specify the array size:

```java
// An array of 20 elements
var myIntArray as int array of 20;
```

The elements of the array will use the default value of its data type when accessed before being assigned a value. More details at the **Data Types** section.

### Multi-dimensional Arrays

A multi-dimensional array in Porado is an array which its elements are also arrays, and they can be declared by adding more `array` keywords after the first `array` keyword:

```java
// Reads as "array of array of integers"
var my2DArray as int array array = [
    [1,2,3],
    [4,5,6],
    [7,8,9]
];


// You can nest even more arrays!
var myMultiArray as int array array array = [
    [
        [1,2,3],
        [4,5,6],
        [7,8,9]
    ],
    [
        [1,2,3],
        [4,5,6],
        [7,8,9]
    ]
]
```

To declare a multi-dimensional empty array, you add an `of` keyword and declare the size of each level of array. To access the elements of a multi-dimensional array, you use multiple square braces `[ ]`, wherein the leftmost is the topmost array, and the rightmost is the bottommost array holding the variable elements:

```java
// Can be read as "an array of 2 arrays of 3 elements"
var myEmptyMultiArray as int array of 2 array of 3;

// Assigns the first array's first element
myEmptyMultiArray[0][0] = 1;

// Assigns the second array's third element
myEmptyMultiArray[1][2] = 1;
```

## Conditionals

Conditional statements in Porado include `if`, `else`, and `switch` statements that help guide control flow when the code runs.

### If statements

In Porado, an `if` statement evaluates a condition to decide whether to run a piece of code or not. An `if` statement must be followed by the condition wrapped in parenthesis `( )` followed by a `then` keyword.

```java
var isTrue as boolean = true;
var myVar as string;

if (isTrue) then myVar = "It is true!";
```

To run multiple lines of code using an `if` statement, define a **code block** by surroudning multiple lines of code with curly brackets `{ }`.

```java
var isTrue as boolean = true;
var myVarArray as string array of 3;

if (isTrue) then {
    myVarArray[0] = "It";
    myVarArray[1] = "is";
    myVarArray[2] = "true!";
}
```

### Else statements

If the condition in an `if` statement evaluates into `false`, the code defined in the `else` statement runs instead. An `else` statement must come only after an `if` statement:

```java
var isTrue as boolean = false;
var myVar as string;

if (isTrue) then myVar = "It is true!"; else myVar = "It is false!";
```


```java
var isTrue as boolean = false;
var myVarArray as string array of 3;

if (isTrue) then {
    myVarArray[0] = "It";
    myVarArray[1] = "is";
    myVarArray[2] = "true!";
} else {
    myVarArray[0] = "It";
    myVarArray[1] = "is";
    myVarArray[2] = "false!";
}
```

### Else If statements

When the condition to an `if` statement evaluates to `false`, you can check for another condition using an `else if` statement. The `then` keyword is also used after the condition in an `else if` statement. An `else if` statement can only be placed after an `if` statement:

```java
var myBool1 as boolean = false;
var myBool2 as boolean = true;
var myName as string;

if (myBool1) then {
    myName = "John"; // This code won't run.
} else if (myBool2) then {
    myName = "Doe"; // This code will run.
}
```

You can combine `if`, `else`, and `else if` statements, but within a single chain, there can only be one initial `if` statement, followed by zero or more `else if` statements, and an optional `else` statement:

```java
var myDayInt as int = 4;
var myDayString as string;

if (myDayInt == 1) then {
    myDayString = "Monday";
} else if (myDayInt == 2) then {
    myDayString = "Tuesday";
} else if (myDayInt == 3) then {
    myDayString = "Wednesday";
} else if (myDayInt == 4) then {
    myDayString = "Thursday";
} else if (myDayInt == 5) then {
    myDayString = "Friday";
} else if (myDayInt == 6) then {
    myDayString = "Saturday";
} else if (myDayInt == 7) then {
    myDayString = "Sunday";
} else {
    myDayString = "Unknown Day";
}
```

You can omit redundant code blocks if there is only one line of code in each statement. This can greatly improve readability:

```java
if (myDayInt == 1) then myDayString = "Monday";
else if (myDayInt == 2) then myDayString = "Tuesday";
else if (myDayInt == 3) then myDayString = "Wednesday";
else if (myDayInt == 4) then myDayString = "Thursday";
else if (myDayInt == 5) then myDayString = "Friday";
else if (myDayInt == 6) then myDayString = "Saturday";
else if (myDayInt == 7) then myDayString = "Sunday";
else myDayString = "Unknown Day";
```

### Switch statements

Long chains of `if`, `else`, and `else if` statements can be cumbersome, so the alternative is to use `switch` statements. To declare a `switch` statement, you need to write a condition surrounded by parenthesis `( )` followed by a `to` keyword. Naturally, you use a **code block** to define multiple `case` statements, declared as `case` followed by parenthesis `( )` with a target value inside.

`Switch` statements in Porado do not fall through; a `switch` statement selects only one branch of code to execute based on a `case` statement with a matching target value. `Case` statements cannot have duplicate target values.

An optional `default` statement, which is a special `case` statement, can be added to the `switch` statement. If no `case` statements are picked by the `switch` statement, the `default` statement is chosen if it exists. There can only be one `default` statement inside a single `switch` statement:

```java
var myDayInt as int = 4;
var myDayString as string;

switch (myDayInt) to {
    case(1) myDayString = "Monday";
    case(2) myDayString = "Tuesday";
    case(3) myDayString = "Wednesday";
    case(4) myDayString = "Thursday"; // Runs this code only.
    case(5) myDayString = "Friday";
    case(6) myDayString = "Saturday";
    case(7) myDayString = "Sunday";
    default myDayString = "Unknown Day";
} 
```

```java
var myDayInt as int = 100;
var myDayString as string;

switch (myDayInt) to {
    case(1) yDayString = "Monday";
    case(2) yDayString = "Tuesday";
    case(3) yDayString = "Wednesday";
    case(4) yDayString = "Thursday";
    case(5) yDayString = "Friday";
    case(6) yDayString = "Saturday";
    case(7) yDayString = "Sunday";
    default myDayString = "Unknown Day"; // Runs this code only.
} 
```

```java
var myDayInt as int = 100;
var myDayString as string;

switch (myDayInt) to {
    case(1) yDayString = "Monday";
    case(1) yDayString = "Monday"; // Compile-time error.
    case(2) yDayString = "Tuesday";
    case(3) yDayString = "Wednesday";
    case(4) yDayString = "Thursday";
    case(5) yDayString = "Friday";
    case(6) yDayString = "Saturday";
    case(7) yDayString = "Sunday";
    default myDayString = "Unknown Day";
} 
```

`Case` statements also allow multiple lines of code to run through the use of **code blocks**, similar to `if`, `else`, and `else if` statements:

```java
var myAnimatCategory as string = "mammals";
var myAnimalNames as string array of 3;

switch (myAnimatCategory) to {
    case("amphibians") {
        myAnimalNames[0] = "frog";
        myAnimalNames[1] = "turtle";
        myAnimalNames[2] = "axolotl";
    }

    case("reptiles") {
        myAnimalNames[0] = "crocodile";
        myAnimalNames[1] = "lizard";
        myAnimalNames[2] = "snake";
    }

    case("mammals") {
        myAnimalNames[0] = "human";
        myAnimalNames[1] = "dog";
        myAnimalNames[2] = "horse";
    }
}
```

## Loops

Loop statements in Porado help control flow by allowing iteration of the same code, making code more compact and dynamic. Loops in Porado include `while`, `do-while`, `until`, `do-until`, `for`, and `repeat` loops.

### While & Do-While loops

`While` and `do-while` loops iterate through a piece of code or **code block** while a condition remains `true`. A `while` loop first checks if the condition is `true` before executing its code; if it is `true` then it runs, otherwise it stops exit the `while` loop, potentially skipping its code. On the other hand, a `do-while` loop first executes its code then it checks its condition if it is true or not, allowing at least one iteration of its code to run.

To declare a `while` loop, it has to be followed by a conditional enclosed in parenthesis `( )` and a `then` keyword. A `while` loop can run both a single piece of code or a **code block** for multiple lines of code:

```java
var myCounter as int = 0;

while (myCounter < 100) then myCounter = myCounter + 1;
```

```java
var myCounter as int = 0;
var mySum as int = 0;

while (myCounter < 5) then {
    mySum = mySum + myCounter;
    myCounter = myCounter + 1;
};
```

Similarly, to declare a `do-while` loop, first write the `do` keyword followed by a line of code or a **code block**. After that, right the `then` keyword and the `while` keyword with a conditional enclosed in parenthesis `( )`:

```java
var myCounter as int = 0;

do myCounter = myCounter + 1 then while (myCounter < 100);
```

```java
var myCounter as int = 0;
var mySum as int = 0;

do {
    mySum = mySum + myCounter;
    myCounter = myCounter + 1;
} then while (myCounter < 5);
```

### Until & Do-Until loops

`Until` and `do-until` loops are exactly like `while` and `do-while` loops, except they only iterate through a piece of code or a **code block** until their condition becomes `true`. In other words, they only iterate through their code if the condition is `false`.

The declaration of an `until` loop is syntactically the same as a `while` loop, except replace the `while` with an `until` keyword:

```java
var myCounter as int = 0;

until (myCounter == 100) then myCounter = myCounter + 1;
```

```java
var myCounter as int = 0;
var mySum as int = 0;

until (myCounter == 5) then {
    mySum = mySum + myCounter;
    myCounter = myCounter + 1;
};
```

Likewise, a `do-until` loop also shared the same syntax as a `do-while` loop, only replacing the `while` keyword with an `until` keyword:

```java
var myCounter as int = 0;

do myCounter = myCounter + 1 then until (myCounter == 100);
```

```java
var myCounter as int = 0;
var mySum as int = 0;

do {
    mySum = mySum + myCounter;
    myCounter = myCounter + 1;
} then until (myCounter == 5);
```

### For loops

A `for` loop in Porado iterates through each element in a given list of elements, such as an array. To declare a `for` loop, first write the `for` keyword followed by the `each` keyword, and then parenthesis `( )` surrounding the **iteration variable**, the `in` keyword, and the **iteration list**. Lastly, write the piece of code or **code block** to execute.

A `for` loop iterates its code a number of times equal to the length of the **iteration list**. Each element in the **iteration list** will be iterated through by the `for` loop by changing the **iteration variable** to the next every end of each iteration:

```java
var mySum as int = 0;

for each (number in [1,2,3,4,5]) mySum = mySum + number;
```

A `for` loop can be used to populate an array:

```java
var myEmptyArray as int array of 30;
var myCounter as int = 0;

for each (element in myEmptyArray) {
    element = myCounter;
    myCounter = myCounter + 1;
}
```

Nested `for` loops can also be used to conveniently access multi-dimensional arrays:

```java
var myEmptyArray as int array of 10 array of 10;
var myCounter as int = 0;

for each (subarray in myEmptyArray) {
    for each (elements in subarray) {
        elements = myCounter;
        myCounter = myCounter + 1;
    }
}
```

```java
var myEmptyArray as int array array array = [
    [
        [1,2,3],
        [4,5,6],
        [7,8,9]
    ],
    [
        [1,2,3],
        [4,5,6],
        [7,8,9]
    ],
    [
        [1,2,3],
        [4,5,6],
        [7,8,9]
    ]
];

var myTotal as int = 0;

for each (subarray1 in myEmptyArray) {
    for each (subarray2 in subarray1) {
        for each (number in subarray2) {
            myTotal = myTotal + number;
        }
    }
}
```

### Repeat loop

A `repeat` loop in Porado is a simple loop that iterates its code a specified number of times. To declare a `repeat` loop, first write the `repeat` keyword followed by an `int` value enclosed in parenthesis `( )`. Lastly, write the piece of code or a **code block** to execute:

```java
var myCounter as int = 0;

repeat (10) {
    myCounter = myCounter + 1;
}
```

You can also include an **iteration variable** in the repeat loop to use in cases where distinquishing the current iteration is important. You can do so by adding an `int` variable declaration with the  repeat amount `int` value both enclosed in the parenthesis `( )` but seperated by comma `,`. The **iteration variable** will be declared locally within the `repeat` loop, and it will be incremented by 1 automatically at each end of iteration.

```java
var myArraySize as int = 30
var myArray as int array of myArraySize;

repeat (var i as int = 0, myArraySize) {
    myArray[i] = i;
}
```

### Break & Continue

The `break` and `continue` keywords in Porado creates more advanced control flow by alterning the behavior of most loops, such as skipping over certain iterations or just ending the loop early.

The `break` keyword tells the loop to stop and exit immediately, skipping all incoming iterations, if any. On the other hand, the `continue` keyword, immediately ends the current iteration and jump to the next one, skipping over any left-over code in the **code block**:

```java
var myVar as int = 0;

while (true) then {
    myVar = myVar + 1;
    if (myVar > 100) then break;
}
```

```java
var myNumArray as int array = [2,4,6,5,8,10,12];

for each (number in myNumArray) {
    if (number % 2 == 0) then continue else break;
}
```

```java
var mySum as int = 0;

repeat (var i as int = 0; 100) {
    if (i % 2 == 0) then continue;
    mySum = mySum + i;
}
```