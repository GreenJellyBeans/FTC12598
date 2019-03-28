use <../common/extrusions.scad>
LARGE = 100;

// This is a wire guide for the "Super Bright 921 LED Bulbs" ordered for the CM Rig
// flash unit - to position and orient the power wires soldered to the tabs and keep
// them from moving and potentially shorting.
module wire_guide() {
    

    wb = 2; // Thickness of separator at it's narrowest (topmost part when oriented flat)
    hb = 4;
    wt = 9; // Width of the guide - side-to-side
    ht = 2;  // Thickness of thinnest portion of base of guide
    xoff = (wt - wb)/2;
    t = 7.5; // Length of base - front to back
    ri = 2; // Wire curvature
    ro = 0;
    
    
    T_channel(wb, hb, wt, ht, xoff, ro, ri, t);
}


wire_guide();