﻿namespace Tests.Diagnostics
{
    public class ForLoopCounterChanged
    {
        public ForLoopCounterChanged()
        {
            for (int a = 0; a < 42; a++)
            {
                a = 0; // Noncompliant
            }

            int a;
            for (a = 0; a < 42; a++)
            {
                a = 0; // Noncompliant
            }

            for (int d = 0, e = 0; d < 42; d++)
            {
                a++;
                d = 0; // Noncompliant
                e = 0;  // Noncompliant
            }

            int g;
            for (int f = 0; f < 42; f++)
            {
                f = 0; // Noncompliant
                g = 0;
                for (g = 0; g < 42; g++)
                {
                    g = 0; // Noncompliant
                    f = 0; // Noncompliant
                }
                f = 0; // Noncompliant
                g = 0; // Compliant
            }

            int g = 0;

            for (int h = 0; h < 42; h++)
            {
                h = // Noncompliant
                h = // Noncompliant
                0;
            }

            g++;
            ++g;
            g = 0;

            for (int i = 0; 0 < 42; i++)
            {
                i++; // Noncompliant
                --i; // Noncompliant
                ++i; // Noncompliant
                i--; // Noncompliant
                i = 1; // Noncompliant
                i += 1; // Noncompliant
                i -= 1; // Noncompliant
                i *= 1; // Noncompliant
                i /= 1; // Noncompliant
                i %= 1; // Noncompliant
                i &= 1; // Noncompliant
                i |= 1; // Noncompliant
                i ^= 1; // Noncompliant
                i <<= 1; // Noncompliant
                i >>= 1; // Noncompliant
                new { i = 0 }.i++;
            }

            for (int j = 0; j < 42; j++)
            {
                for (k = 0; j++ < 42; k++) // Noncompliant
                {
                }
            }

            for (int i = 0; i < 42; i++)
            {
                (int)i;
            }

            var s1 = new { i = 0, j = new int[] { 0, 1 } };

            for (s1.i = 0; s1.i < 3; s1.i++)
            {
                s1.i = 1; // Noncompliant
                s1.j[0]++;
            }

            for (s1.j[1] = 0; s1.j[1] < 3; s1.j[1]++)
            {
                s1.i++;
                s1.j[0]++;
                s1.j[1]++; // Not detected
            }

            var a1 = new int[] { 0, 1 };

            for (a1[0] = 0; a1[0] < 3; a1[0]++)
            {
                a1[0] = 1; // Not detected
                a1[1] = 1;
            }

            int i = 0;
            for (; i > 0; i++)
            {
                i = 1;
            }

            for (i = 0; i < 3; i++)
            {
                s1.i = 1;
            }

            foreach (int element in myArray)
            {
                element = 0;
            }
        }
    }
}
