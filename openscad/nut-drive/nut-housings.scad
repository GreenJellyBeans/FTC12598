// Housings to hold hexagonal nut for a "nut drive" two nuts with
// reverse threads on a threaded rod.
use <../common/extrusions.scad>
EPSILON = 0.01;
LARGE = 100;

function poly_coords(order, r=1)  = 
    let(angles=[ for (i = [0:order-1]) i*(360/order) ])
 	[ for (th=angles) [r*cos(th), r*sin(th)] ];

    
 module regular_polygon(order, r=1){
 	polygon(poly_coords(order, r));
 }


block_w = 30;
block_d = 30;
block_h = 10;
block_ro = 3;
 
module center_piece() {
    
    hex_dia = 20;
    hole_r = 2.05;
    hole_major_dia = block_d  - hole_r;
    hole_centers = poly_coords(4, hole_major_dia/2);
    punch_h = block_h + 2*EPSILON;
    
    difference() {
        oblong(block_w, block_d, block_ro, block_h);
        translate([block_w/2, block_d/2, -EPSILON]) {
            linear_extrude(height = block_h + 2*EPSILON) regular_polygon(6, hex_dia/2);
            rotate([0, 0, 45]) for (i = [0:3]) {
                translate(hole_centers[i]) 
                    cylinder(r=hole_r, h = punch_h);
            }
        }
    }
}


$fn = 50;
center_piece();