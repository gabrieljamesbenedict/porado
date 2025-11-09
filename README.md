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

Conditional statements in Porado 