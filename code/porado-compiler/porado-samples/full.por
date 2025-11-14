num as int = 10;
name as string = "Gabriel";
key as char = 'A';
price as float = 3.99;
isDay as boolean = true;

(10 * 5) + (100 / (25 - 5)) % 3;
(100 - (25 * 3)) + 4;
((20 / 4) % 3) * (5 + 5);
50 + 50 - (10 * 10) / 2;

x == 10;
a > 0 and a <= 100;
(num + 10) * 5 > 100;
(x > 3) and (y <= 10) and (z == 5);

nums as array of 5 int = [1,2,3,4,5];
otherNums as array of int = nums;
print(nums[0]);
print(nums[0]);
print(nums[0]);

switch (num) {
    case (3): {
        num = 2;
    }
    case (2): {
        num = 1;
    }
    case (1): num = 0;
    default: num = 3;
}

while (num < 50) then {
    num = num + 1;
}

until (num == 100) then {
    num = num + 1;
}

for (each num in nums) {
    print(num);
}

repeat 5 {
    print("HELLO");
}

add as function accepts (num1 as int, num2 as int) returns int {
    return num1 + num2;
}

num = add(10,20);