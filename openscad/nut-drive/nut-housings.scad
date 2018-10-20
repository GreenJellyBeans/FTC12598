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
block_ro = 3;

hole_r = 2.05;
holes_major_dia = block_d  - hole_r;
 
 center_h = 10;
 end_h = 3;


// about center
module block2d(w, r) {
    offset(r = r) square([w-2*r, w-2*r], center=true);
}


// about center
module holes2d(major_dia, r) {
    hole_centers = poly_coords(4, major_dia/2);
    rotate([0, 0, 45]) for (i = [0:3]) {
                translate(hole_centers[i]) 
                    circle(r);
            }
}

module center_piece() {
    
    hex_dia = 20;
    
    linear_extrude(height = center_h) {
        difference() {
             block2d(block_w, block_ro);
            regular_polygon(6, hex_dia/2);
            holes2d(holes_major_dia, hole_r);
        }
    }
}

module end_piece() {
    linear_extrude(height = end_h) {
        center_hole_r = 5;
        anchor_offset = 5; // from top of block
        anchor_hole_r = hole_r;
        anchor_wall = 3; // min wall surrounding anchor
        anchor_center = [0, block_w/2 + anchor_offset];
        difference() {
                 //linear_extrude(height = block_h + 2*EPSILON) 
            hull() {
                block2d(block_w, block_ro);
                translate(anchor_center)
                    circle(anchor_wall + anchor_hole_r);
            }
            holes2d(holes_major_dia, hole_r);
            circle(center_hole_r);
            translate(anchor_center)
                circle(anchor_hole_r);
            
        }
    }
}


$fn = 50;
center_piece();
translate([block_w + 10, 0, 0]) end_piece();
translate([0, block_w + 10, 0]) end_piece();