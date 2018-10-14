# Known Issues and Work Items
1. Block elements are not impervious to robots. If the corner of the robot is close to the
 corner of a block, the robot can nudge inside. This is because of a quirk in collision computations
 with walls detailed in `NOTES.md` entry "October 11, 2018-C" 
 "Tapering thickness of non-boundary walls at their ends" and earlier notes.
1. Add higher-level sensor access methods that report position, color sensor values and
 encoder values, after they have been perturbed by noise.
1. Scan code and .md files for typos
