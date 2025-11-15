### Strict Variables

A strict variable is a variable that doesn't use default values. When the value of a strict variable is accessed before it was assigned a value, a runtime error is occured.

To declare a variable as a strict variable, add the `strict` modifier keyword in the variable declaration:

```js
num as strict int;

print(num); // Runtime error
```

### Fixed Variables

A fixed variable is immutable and can only be assigned a value only once. Attempting to assign a new value to a fixed variable after it was already assigned a value will make a runtime error occur.

To declare a variable as a fixed variable, add the `fixed` modifier keyword in the variable declaration:

```js
num as fixed int;
num = 10;
num = 20; // Runtime error
```

***

### Multi-Dimensional Arrays

Arrays can store any data, even other arrays. Storing arrays inside of arrays makes multi-dimensional arrays. All child arrays of a parent array must have the same length. Multi-dimensional arrays can be used to mimic tables with rows and columns.

To declare a multi-dimensional array, replace the data type to an array type:

```js
// num is an array of 5 arrays of 10 integers
// 50 total elements
nums as array of 5 array of 10 int;

// nums is an array of 5 arrays of 10 arrays of 15 integers.
// 750 total elements
nums as array of 5 array of 10 array of 15 int;
```

You can initialize the multi-dimensional array with an existing list to omit verbose code:

```js
nums as array of array of int = [
    [1,2,3],
    [4,5,6],
    [7,8,9]
];
```

Values in multi-dimensional arrays can be accessed, read, and wrote to using chained array access operators `[ ]` to refernce the element. The leftmost access operator references the top array and the rightmost access operator references the element.

```js
nums as array of array of int = [
    [1,2,3],
    [4,5,6],
    [7,8,9]
];

nums[0][0] = 10; // Assign to 10
num1 = nums[0][1] // nums1 == 2
num2 = nums[1][1] // nums5 == 5
num3 = nums[3][1] // Runtime error
```

### Array and Array Element Modifiers

Modifiers that are used for variables can also be applied for arrays and its elements.

The `strict` keyword when used on an array declaration turns the array into a strict array. A strict array will cause a runtime error when accessing any of its elements before all of its elements are explicitly initialized.

Using the `strict` modifier on its elements turns them into strict variables. Accessing an element before it is initialized will cause a runtime error.

```js
nums as strict array of 5 int;
print(nums[0]); // Runtime error
nums[0] = 0;
print(nums[0]) // Still runtime error; all elements need to initialized
```

```js
nums as array of 5 strict int;
print(nums[0]); // Runtime error
nums[0] = 0;
print(nums[0]) // No error
print(nums[1]); // Runtime error; element at index 1 is not yet initialized
```

The `fixed` keyword when used on an array declaration turns the array into a fixed array. A fixed array can be explicitly assigned a value only once. Assigning more than once makes a runtime error occur.

When set as a modifier for the array elements, the elements are become `fixed` variables and cannot be assigned a new value after it was already assigned one.

```js
nums as fixed array of 5 int = [1,2,3,4,5];
nums = [6,7,8,9,0] // Runtime error
nums[0] = 100 // No error: only the array itself is immutable
```

```js
nums as  array of 5 fixed int = [1,2,3,4,5];
nums[0] = 100 // Runtime Error
nums = [6,7,8,9,0] // No error: only the elements are immutable
```

***

