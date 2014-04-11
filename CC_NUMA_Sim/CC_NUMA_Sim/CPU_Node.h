
/*
Each CPU has a corresponding cache.  The processor has three registers
and the cache has 4 entries.  Each entry has three fields.  The CPU
unit performs cache search for data reads and writes.
*/

class CPU
	{
	public:

		CPU();
		bool hit(int); // cache searching function

		int P[3]; // Processor registers s0, s1, s2
		struct cache {
			bool valid;
			int tag;
			int data;
			};
		cache H[4]; // 4 cacHe entries
		int datum; // temp storage for hit case

	};
