/*
Compilation:  The following documents must be in the same directory:
	CPU_Node.h, CPU_Node.cpp, SMP_Node.h, SMP_Node.cpp, CC_NUMA_Node.h,
	CC_NUMA_Node.cpp, CC_NUMA_Sim.cpp
Compile the following files
	CPU_Node.cpp, SMP_Node.cpp,	CC_NUMA_Node.cpp, CC_NUMA_Sim.cpp


This is the driver program for the menu-based simulation of a mini CC-NUMA 
architecture with directory-base cache coherency control using 
write-invalidate protocol.  

This simulation is user interactive from the command prompt.  The user inputs
instructions for the simulator to execute, and the user can request to display
data structure contents.  Upon quit, statistics are displayed.

Hardware Sub-Structures:  4 SMP nodes, for each node, there is one memory unit
and corresponding directory, and two CPU and cache units.  Additional 
documentation for these sub-structures are provide at the sub-structure level.
*/

#include "CC_NUMA_Node.h"

int main() {

	CC_NUMA mini;
	mini.menu();
	getchar();

	return 0; }
