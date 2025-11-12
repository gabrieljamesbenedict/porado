num as int = 42;
otherNum as int = -10 + 5 * -1 + (++num * num--);

// Stress Test
incTest as int = ++num + num++ + num++ + ++num;
decTest as int = --num - num-- - num-- - -(--num);
negTest as int = -12 - -34 - -45 - -56 + -78;

// Arithmetic operations
sum as int = num + 10;
diff as int = num - 5;
prod as int = num * 2;
quot as float = num / 3;
mod as int = num % 4;

sum2 as int = 1+2+3+4+5;
diff2 as int = -5-4-3-2-1;
prod2 as int = 1*2*3*4*5;
qout2 as float = 100/1/2/3/4;
mod2 as int = 15%4%2;

// Increment / Decrement
num++;
--diff;