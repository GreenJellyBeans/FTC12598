//
//  Strain relief for FTC robot main power switch
//

use <../common/extrusions.scad>

$fn = 20;
    
// Switch strain relief
module switch_sr() {

    LARGE = 1000;
    switch_w = 30;
    switch_h = 20;
    switch_d = 30;
    wall_t = 2;
    base_w = switch_w + 2*wall_t;
    base_t = 3;
    ext_w = 10;
    ext_off = 5;
    base_d = switch_d;
    ext_d = 10;
    base_r = 5;
    // Base
    T_channel(ext_w, ext_d, base_w, base_d, ext_off, base_r, base_t);
    
    wall_h = 15;

    // Switch walls
    ro = 3;
    ri = 0;
    translate([0, ext_d+base_d, 0]) rotate([90, 0, 0])
        O_channel(base_w, wall_h+base_t, switch_w, LARGE, wall_t, base_t, ro, ri,base_d);
    
    // Lug support
    /*
    lug_h = 10;
    lug_gap = 5;
    lug_pad = 1;
    translate([ext_off, ext_d, 0]) rotate([90, 0, 0])
    O_channel(ext_w, lug_h+base_t, lug_gap, LARGE, ext_off/2, lug_pad+base_t, 0, ro, ext_d);
    */
    lug_h = 5;
    lug_center_w = 5;
    lug_r = 2;
    lug_pad_h = 0.5;
    lug_gap_d = 0.5;
    translate([ext_off+ext_w, ext_d-lug_gap_d, lug_h+base_t+lug_pad_h]) rotate([90, 180, 0])
        T_channel(lug_center_w, lug_h, ext_w, base_t+lug_pad_h, 
        ext_off-lug_center_w/2, lug_r, ext_d-lug_gap_d);
}



switch_sr();
