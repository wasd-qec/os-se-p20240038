# Class Activity 7 - Reasoning About Deadlock

- **Student Name:** [Rith Chankolboth]
- **Student ID:** [p20240038]
- **My personalization:** a = 8, b = 3

---

## Task 1 — Resource Allocation Graphs

### Part A
**Graph 1 — my prediction:**
-theres a cycle
-theres a dealock
- P0->R1->P1->R2->P2->R0->P0
![Graph 1](screenshots/task1_graph1.png)
Matched the tool? yes

**Graph 2 — my prediction:**
-   Theres no cycle
-   Theres no deadlock
-   P2 will finish first because it doesnt wait for anything else, other process wait for others like a train but once P2 finish, P1 will finish then P0.
![Graph 2](screenshots/task1_graph2.png)
Matched the tool? yes

### Part B
**(i) Deadlocked 3×3 graph** — edges I used + why it deadlocks:
by creating a cycle where a process request another's resources and the other process request the first process's resources, they will be stuck in a deadlock.
![Built deadlock](screenshots/task1_build_deadlock.png)

**(ii) No-cycle graph (≥4 nodes, ≥1 request)** — why it is deadlock-free:
i created 4 process cuz its minimum of 4 process, then i made only one resource, since all process only need this one resource there will be no deadlock because when every process will  just take turns
![Built no-cycle](screenshots/task1_build_nocycle.png)

---

## Task 2 — Cycle ≠ Deadlock

**Warm-up (built-in examples)**
1. Why the "Cycle, NO deadlock" example is not deadlocked:
Even though theres a cycle, there were enough resource to complete the P3 which release resource to complete others step by step.
 -P3 finish first
 -P2 then was able to complete after receiving R2 then release R1 
 -P1 then was able to complete aftere receiving R1 from P2.
2. The single change that causes deadlock:
Theres a cycle, but P3 request R1 which the system doesn't currently have, one process needs to finish to release resource to complete others but in this case there are no process that can be complete, so no resource were release.

**Part A — given scenario**
- Available = Total − ΣAlloc =
Available = [0, 0, 0]
P3 then finish(it doesnt request)
Available = [1,0,1]
then we check P1(request R2 but theres no R2 available)
P2 request R1 which we have so P2 finish releasing [0,1,1]
Available = [1,0+1,1+1] = [1,1,2]
Finally we have R2 to complete P1 releasing [1,0,0]
Available = [1+1,1,2] = [2,1,2] which is the amount of instances you state
- The cycle (as a path): P1->R2->P2->R1->P1 but we can still finish because P3 can finish on its own and release R1

| Step | Process | Why Request ≤ Work | Work after release |
|------|---------|--------------------|--------------------|
| 1    | P3      | Request [0,0,0] <= Work [0,0,0] | [0,0,0] + [1,0,1] = [1,0,1] |
| 2    | P2      | Request [1,0,0] <= Work [1,0,1] | [1,0,1] + [0,1,1] = [1,1,2] |
| 3    | P1      | Request [0,1,0] <= Work [1,1,2] | [1,1,2] + [1,0,0] = [2,1,2] |

Conclusion: NOT deadlocked (finishing order: P3 -> P2 -> P1)
![Given scenario](screenshots/task2_given_nodeadlock.png)
After changing P3's request to `0 1 0` — my prediction + why it deadlocks (reduction terms):
Prediction: The system will deadlock.
      Initially, Work = Available = [0,0,0].
      Before, P3 could finish first cuz it had no requests [0,0,0] which is <= Work [0,0,0], then it releases resources so other processes could run.
      But now, P3 requests [0,1,0]. If we check all requests:
      - P1 requests [0,1,0]
      - P2 requests [1,0,0]
      - P3 requests [0,1,0]
      None of these requests are <= Work [0,0,0], so we can't even start the reduction. We are immediately stuck in a deadlock since no process can finish to release their resources.
![Given scenario, deadlock](screenshots/task2_given_deadlock.png)

**Part B — my own scenario**
![Cycle, no deadlock](screenshots/task2_cycle_nodeadlock.png)
My change that caused deadlock + why (reduction terms):
I've removed the an instance of R2 which then lead to P3 unable to finish, so it became a deadlock.
![Deadlock](screenshots/task2_deadlock.png)

---

## Task 3 — Banker's Algorithm (my personalized scenario)

- Max[P0][A] = 7 + (a mod 3) = 7 + (8 mod 3) = 7+2=9   Max[P2][C] = 2 + (b mod 4) = 2 + (3 mod 4) = 2+3=5
- **Need matrix:**
Need = Max - Allocation
P0: [9, 5, 3] - [0, 1, 0] = [9, 4, 3]
P1: [3, 2, 2] - [2, 0, 0] = [1, 2, 2]
P2: [9, 0, 5] - [3, 0, 2] = [6, 0, 3]

Need Matrix:
P0: [9, 4, 3]
P1: [1, 2, 2]
P2: [6, 0, 3]
- **Available:** Available = Total - Sum(Allocation)
Available = [10, 5, 7] - [0+2+3, 1+0+0, 0+0+2]
Available = [10, 5, 7] - [5, 1, 2] = [5, 4, 5]

**Safety trace (by hand):**

| Step | Process | Why Need ≤ Work | Work after release |
|------|---------|-----------------|--------------------|
| 1    | P1      | Need [1,2,2] <= Work [5,4,5] | [5,4,5] + [2,0,0] = [7,4,5] |
| 2    | P2      | Need [6,0,3] <= Work [7,4,5] | [7,4,5] + [3,0,2] = [10,4,7] |
| 3    | P0      | Need [9,4,3] <= Work [10,4,7] | [10,4,7] + [0,1,0] = [10,5,7] |

Conclusion: SAFE  P1 -> P2 -> P0
![Safety check](screenshots/task3_safety.png)
Matched the tool? yes

**Request I predicted GRANTED:** P1 requests [1, 0, 0]
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
![Grant](screenshots/task3_request_grant.png)

**Request I predicted DENIED:** P2 requests [5, 0, 0]
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
![Deny](screenshots/task3_request_deny.png)

---

## Task 4 — Semaphores and Deadlock

**Case 1 (s1=s2=s3=1) — my answer:** **NO** (cannot deadlock)
- **Explanation:** P1 holds s1 and requests s2. P2 holds s2 and requests s3. Since P1 holds s1, P3 cannot even start so s3 is available. Then P2 takes s3 and releases s2 and s3. Since s1 is still held, P1 gets s2 first. Then P1 finishes and releases s1. Then P3 can finish.
![Case 1](screenshots/task4_case1.png)
- **Tool confirmed?** Yes, the tool confirms there is no cycle and no deadlock.

**Case 2 (s1=s2=s3=1) — my answer:** **YES** (can deadlock)
- **Explanation:** P1 holds s1. In case P2 holds s2 then it works like Case 1, but in the worst case P3 holds s2 then it creates a deadlock because P3 will also hold s3 but can never get s1 (which is held by P1), while P1 is waiting for s2.
![Case 2](screenshots/task4_case2.png)
- **Tool confirmed?** Yes, the tool confirms a deadlock cycle is formed.

**Case 3 (s1=2) — my answer:** **NO** (cannot deadlock)
- **Explanation:** It is the same as Case 2, but even if P3 holds s2 it is fine because there is an extra instance of s1 to finish P3 and move on to others.
![Case 3](screenshots/task4_case3.png)
- **Tool confirmed?** Yes, the tool confirms the system is not deadlocked.

---

## Task 5 — Applied Concepts

1. **The four necessary conditions for deadlock mapped to a Smart Kitchen / Baking Station scenario:**
   - **Mutual Exclusion:** Only one baker can use the Whisk or Bowl at a time.
   - **Hold and Wait:** Baker A holds the Whisk while waiting for the Bowl, refusing to release the Whisk.
   - **No Preemption:** Baker A cannot forcefully snatch the Bowl from Baker B; they must wait until Baker B willingly sets it down.
   - **Circular Wait:** Baker A holds the Whisk and waits for the Bowl, while Baker B holds the Bowl and waits for the Whisk.
   - **Easiest condition to remove:** Hold and Wait. We can require that bakers must obtain both the Whisk and the Bowl at the same time before starting. If they cannot get both, they must hold none.
   - **Cost:** Reduced resource utilization. A whisk might sit idle while a baker is waiting for a bowl to become available.

2. **Why a cycle in a multi-instance RAG does not prove deadlock:**
   In a single-instance RAG, a resource can only be held by one process, so a cycle means a closed chain of processes waiting on each other with no external way to break the wait. In a multi-instance system, a process in the cycle might be waiting on a resource that has a spare instance held by a process *outside* the cycle; that external process can finish and release its instance, breaking the cycle.

3. **Difference between unsafe state and deadlocked state (+ one-line example):**
   A deadlocked state is one where processes are already blocked and cannot proceed. An unsafe state is not necessarily deadlocked yet, but the operating system cannot guarantee that it can avoid a deadlock if processes request their maximum declared resources.
   *One-line example:* Process P0 holds 2 units of R, has a Max demand of 3, and Available = 0; it is currently running but will deadlock if it requests its remaining 1 unit.

4. **Deadlock avoidance (Banker's) vs. deadlock detection + recovery:**
   - **Deadlock Avoidance (Banker's):**
     - *Cost:* High runtime overhead because every resource request must run safety algorithm checks.
     - *System:* Safety-critical systems (e.g., flight control software, medical devices) where deadlocks are completely unacceptable.
   - **Deadlock Detection + Recovery:**
     - *Cost:* Overhead of periodically running detection algorithms and the cost of recovery (e.g., aborting processes, rolling back transactions).
     - *System:* Database management systems (DBMS) or general-purpose operating systems where deadlocks are rare and the cost of avoidance is too restrictive.

5. **Why Banker's requires maximum demand in advance and its real-world problem:**
   The Banker's Algorithm needs the maximum demand to simulate worst-case scenarios and ensure the system always has a safe path to allocate resources.
   *Real-world problem:* In general-purpose systems, it is extremely difficult or impossible for processes (like web browsers or compilers) to predict their maximum resource needs in advance, as their execution path depends on user input or dynamic data.

---

## Reflection

This activity helped clarify that a cycle in a resource allocation graph is only a guarantee of deadlock when resources are single-instance. In multi-instance systems, the presence of a cycle is a necessary condition, but not a sufficient one, because a process inside the cycle can be unblocked by resource releases from processes outside the cycle.

Additionally, it highlighted the practical challenges of deadlock avoidance: while algorithms like the Banker's Algorithm are theoretically elegant, the requirement to know maximum resource demands in advance makes them impractical for general-purpose operating systems, where deadlock detection and recovery (or even ignoring the problem entirely, like the ostrich algorithm) are often more pragmatic choices.