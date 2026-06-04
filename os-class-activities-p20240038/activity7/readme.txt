Task 1

Part A

Graph 1:
My prediction:
-	Theres a cycle
-	Theres a dealock
-	P0->R1->P1->R2->P2->R0->P0

Graph 2:
My prediction:
-   Theres no cycle
-   Theres no deadlock
-   P2 will finish first because it doesnt wait for anything else, other process wait for others like a train but once P2 finish, P1 will finish then P0.

Part b
i) by creating a cycle where a process request another's resources and the other process request the first process's resources, they will be stuck in a deadlock. 
ii)i created 4 process cuz its minimum of 4 process, then i made only one resource, since all process only need this one resource there will be no deadlock because when every process will  just take turns

Task 2:

Warm up:

1. Even though theres a cycle, there were enough resource to complete the P3 which release resource to complete others step by step.
 -P3 finish first
 -P2 then was able to complete after receiving R2 then release R1 
 -P1 then was able to complete aftere receiving R1 from P2.

2. Theres a cycle, but P3 request R1 which the system doesn't currently have, one process needs to finish to release resource to complete others but in this case there are no process that can be complete, so no resource were release.

PartA

1. Available = [0, 0, 0]
  P3 then finish(it doesnt request)
  Available = [1,0,1]
  then we check P1(request R2 but theres no R2 available)
  P2 request R1 which we have so P2 finish releasing [0,1,1]
  Available = [1,0+1,1+1] = [1,1,2]
  Finally we have R2 to complete P1 releasing [1,0,0]
  Available = [1+1,1,2] = [2,1,2] which is the amount of instances you state

2. P1->R2->P2->R1->P1 but we can still finish because P3 can finish on its own and release R1

3. 
   | Step | Process chosen | Why Request <= Work? | Work after it releases |
   |------|----------------|----------------------|------------------------|
   | 1    | P3             | Request [0,0,0] <= Work [0,0,0] | [0,0,0] + [1,0,1] = [1,0,1] |
   | 2    | P2             | Request [1,0,0] <= Work [1,0,1] | [1,0,1] + [0,1,1] = [1,1,2] |
   | 3    | P1             | Request [0,1,0] <= Work [1,1,2] | [1,1,2] + [1,0,0] = [2,1,2] |

   Conclusion: NOT deadlocked (finishing order: P3 -> P2 -> P1)

4.Yes

5. Prediction: The system will deadlock.
      Initially, Work = Available = [0,0,0].
      Before, P3 could finish first cuz it had no requests [0,0,0] which is <= Work [0,0,0], then it releases resources so other processes could run.
      But now, P3 requests [0,1,0]. If we check all requests:
      - P1 requests [0,1,0]
      - P2 requests [1,0,0]
      - P3 requests [0,1,0]
      None of these requests are <= Work [0,0,0], so we can't even start the reduction. We are immediately stuck in a deadlock since no process can finish to release their resources.
PartB
I've removed the an instance of R2 which then lead to P3 unable to finish, so it became a deadlock.

Task3
Max[P0][A] = 7 + (a mod 3) = 7 + (8 mod 3) = 7+2=9
Max[P2][C] = 2 + (b mod 4) = 2 + (3 mod 4) = 2+3=5

1. Need matrix:
   Need = Max - Allocation
   P0: [9, 5, 3] - [0, 1, 0] = [9, 4, 3]
   P1: [3, 2, 2] - [2, 0, 0] = [1, 2, 2]
   P2: [9, 0, 5] - [3, 0, 2] = [6, 0, 3]

   Need Matrix:
   P0: [9, 4, 3]
   P1: [1, 2, 2]
   P2: [6, 0, 3]

2. Available:
   Available = Total - Sum(Allocation)
   Available = [10, 5, 7] - [0+2+3, 1+0+0, 0+0+2]
   Available = [10, 5, 7] - [5, 1, 2] = [5, 4, 5]

3. Safety check:
   Work = Available = [5, 4, 5]

   | Step | Process chosen | Why Need <= Work? | Work after it releases |
   |------|----------------|-------------------|------------------------|
   | 1    | P1             | Need [1,2,2] <= Work [5,4,5] | [5,4,5] + [2,0,0] = [7,4,5] |
   | 2    | P2             | Need [6,0,3] <= Work [7,4,5] | [7,4,5] + [3,0,2] = [10,4,7] |
   | 3    | P0             | Need [9,4,3] <= Work [10,4,7] | [10,4,7] + [0,1,0] = [10,5,7] |

   Conclusion: SAFE  P1 -> P2 -> P0

4. safe

5. Requests:
   - Request 1 (Granted):
     P1 requests [1, 0, 0]
     Checks:
     1. Request [1, 0, 0] <= Need [1, 2, 2] (Yes)
     2. Request [1, 0, 0] <= Available [5, 4, 5] (Yes)
     3. Safety of tentative state:
        Work = Available - Request = [5, 4, 5] - [1, 0, 0] = [4, 4, 5]
        Allocation[P1] becomes [2, 0, 0] + [1, 0, 0] = [3, 0, 0]
        Need[P1] becomes [1, 2, 2] - [1, 0, 0] = [0, 2, 2]
        Reduction order:
        - P1: Need [0, 2, 2] <= Work [4, 4, 5] -> Work becomes [4, 4, 5] + [3, 0, 0] = [7, 4, 5]
        - P2: Need [6, 0, 3] <= Work [7, 4, 5] -> Work becomes [7, 4, 5] + [3, 0, 2] = [10, 4, 7]
        - P0: Need [9, 4, 3] <= Work [10, 4, 7] -> Work becomes [10, 4, 7] + [0, 1, 0] = [10, 5, 7]
        Safe sequence P1 -> P2 -> P0.

   - Request 2 (Denied):
     P2 requests [5, 0, 0]
      Checks:
      1. Request [5, 0, 0] <= Need [6, 0, 3] (Yes)
      2. Request [5, 0, 0] <= Available [5, 4, 5] (Yes)
      3. Safety of tentative state:
         Work = Available - Request = [5, 4, 5] - [5, 0, 0] = [0, 4, 5]
         No processes can satisfy Need <= Work:
         - Need[P0] = [9, 4, 3] > Work [0, 4, 5]
         - Need[P1] = [1, 2, 2] > Work [0, 4, 5]
         - Need[P2] = [1, 0, 3] > Work [0, 4, 5] (since Need[P2] became [6,0,3] - [5,0,0] = [1,0,3])
         No processes can finish, UNSAFE;
Task4

Case 1:
P1 hold s1, request s2
P2 hold s2 and request s3,
Since p1 hold s1, p3 cannot even start so s3 is available
then P2 takes s3 and release s2 and s3 since s1 is still held then P1 get s2 first
then P1 finish and release s1 
then P3 can finish 

Case 2:
P1 is fine
In case P2 hold s2 then it works like case 1 but in 
worse case P3 hold s2 then it create a dealock becasue P3 will also hold s3 but can never get S1 so it becomes a deadlock

Case 3:
Its the same as case2 but even if P3 held s2 it's fine cuz theres an extra instance of s1 to finish P3 and move on to others