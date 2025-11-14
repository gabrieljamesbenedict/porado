
num as int = a * b - c / d + (1 + 2);
num2 as int = 1 + 2 - 3 * 4 / 5 % 1 + (1 + 2 + 3);
nums as array of 1+2+3 int;
nums2 as array of int = [1,2,3,4,5,6];
nums3 as array of int = nums2;

addNums as function accepts (num1 as int, num2 as int) returns int {
    return num + num1;
}

getNum as function returns int {
    return num;
}

setNum as function accepts (number as int) {
    num = num1;
}