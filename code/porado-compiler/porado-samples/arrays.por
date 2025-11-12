// 1D Array
nums as array of 5 int = [1,2,3,4,5];
nums[0] = 10;       // Write
num1 as int = nums[1];     // Read

// Multi-Dimensional Array
matrix as array of 2 array of 3 int = [
    [1,2,3],
    [4,5,6]
];
matrix[0][0] = 10;
value as int= matrix[1][2];  // value == 6

// Strict Array
strictArray as strict array of 3 int;
strictArray[0] = 1;
strictArray[1] = 2;
strictArray[2] = 3;

// Fixed Array
fixedArray as array of 3 fixed int = [1,2,3];
fixedArray[0] = 10;   // Runtime error