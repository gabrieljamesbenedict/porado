// Basic variables
num as int = 11;
name as string = "Gabriel";
grade as char = 'A';
price as float = 3.99;
isDay as boolean = true;
uninit as int;



print(num);
print(name);
print(grade);
print(price);
print(isDay);
print(uninit);

num = 121;

if (num % 2 == 0) then {
    print("EVEN");
} else if (num % 2 != 0) then {
    print("ODD");
}

switch (grade) {
    case ('A'): {print("PASS");}
    case ('B'): {print("PASS");}
    case ('C'): {print("PASS");}
    case ('D'): {print("PASS");}
    default: print("FAIL");
}

nums as array of 10 int = [1,2,3,4,5,6,7,8,9,0];

print("While");
i as int = 0;
while (i < 10) then {
    print(nums[i]);
    i++;
}

print("Until");
j as int = 0;
until (j == 9) then {
    print(nums[j]);
    j++;
}

print("For");
for (each n in nums) {
    print(n);
}

print("Repeat");
repeat (10)  {
    print("HELLOWORLD");
}

add as function accepts (num1 as int, num2 as int) returns int {
    return num1 + num2;
}

setNum as function accepts (num1 as int) {
    num = num1;
}

getNum as function returns int {
    return num;
}

print(add(10,20));

setNum(999);

print(num);
