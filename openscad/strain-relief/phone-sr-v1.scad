//
//  Strain relief for FTC robot phones - driver station and robot controller
//

use <../common/extrusions.scad>

$fn = 20;

thick = 3; // thickness of walls and base
LARGE = 100; // Something larger than any demension - for O to become U
EPSILON = 0.1; // Small emount to extend so that unions and intersections are clean

// Phone side - just the bottom
module phone_part(base_w) {
    //base_w = 29; // width - x
    base_d = 20; // depth - y
    ro = 1; // outside-facing radii
    cube([base_w, base_d, thick]);
}

// Support below usb cable - depth - in y-direction -  is {d}
module cable_support(cable_w, d) {
    //cable_w = 13; // width - x 
    cable_h = 11; // height - z
    cable_center_w = 5; // width of center protrusion
    cable_r = 1; // radii of curve in center protrusion
    cable_pad_h = 5; // the base of the lug is slightly elevated (z) from the switch base
    cable_gap_d = 10; // a small gap in x between the start of the center protrusion.
                     // This is because the lugs are slightly taller (z) where they
                     // attach to the switch
    
    // The difference is to round the base of the lug - so that tape does not have to
    // wrap around a sharp corner.
    difference() {
        union() {
            cube([cable_w, d+EPSILON, thick]);
            translate([0, d-cable_gap_d, 0]) rotate([90, 0, 0])
                O_channel(cable_w, cable_h, cable_center_w, LARGE, 
                (cable_w - cable_center_w)/2, cable_pad_h+thick,
                cable_r, cable_r,
                d - cable_gap_d);
        }
        translate([-thick, d+EPSILON, -thick]) rotate([90, 0, 0])
            O_channel(cable_w+2*thick, thick+thick, cable_w, LARGE, 
                thick, thick,
                0, cable_r, d+2*EPSILON);
    }
}


module switch_strain_relief() {
    base_w = 55;
    cable_w = 10;
    cable_d = 30; // depth - y - of lug
    cable_off = (base_w - cable_w)/2; // offset in x of lug position
    translate([0, cable_d, 0]) phone_part(base_w);
    translate([cable_off, 0, 0]) cable_support(cable_w, cable_d);
}


switch_strain_relief();


