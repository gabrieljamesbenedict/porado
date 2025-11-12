// Functions
factorial as function accepts (n as int) returns int {
    if (n <= 1) then return 1;
    return n * factorial(n - 1);
}

fact = factorial(5);