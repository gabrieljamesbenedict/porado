/*num as int = a * b - c / d + (1 + 2);
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

if (num == 1) then num + 1;
else if (num == 2) then num + 2;
else if (num == 3) then num + 3;
else if (num == 4) then num + 4;
else if (num == 5) then num + 5;
else then num * 10;


if (num > 0) then {
    num + 1;
    num + 1;
    num + 1;
} else then num - 1;


switch (num) {
    case(1): num = 1;
    case(2): num = 2;
    case(3): num = 3;
    case(4): num = 4;
    default: {
        num = 1;
        num = 2;
        num = 3;
    }
}
*/

while (num < 100) then {
    num + 1;
}

until (num = 100) then {
    num - 1;
}

do {

} while (false);

do {

} while (true);

for (each num in nums) {
    num = 10;
}

repeat (5) {
    num + 1;
}

repeat (5) with i as int {
    num + i;
}

