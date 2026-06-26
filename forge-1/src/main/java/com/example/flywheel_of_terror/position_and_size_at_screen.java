package com.example.flywheel_of_terror;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class position_and_size_at_screen {
   private int width_offset;
   private int height_offset;
   private int width_size;
   private int height_size;

   public position_and_size_at_screen(int width_offset, int height_offset, int width_size, int height_size) {
      this.width_offset = width_offset;
      this.height_offset = height_offset;
      this.width_size = width_size;
      this.height_size = height_size;
   }

   public void set_width_offset(int new_value) {
      this.width_offset = new_value;
   }

   public void set_height_offset(int new_value) {
      this.height_offset = new_value;
   }

   public void set_width_size(int new_value) {
      this.width_size = new_value;
   }

   public void set_height_size(int new_value) {
      this.height_size = new_value;
   }

   public int get_width_offset() {
      return this.width_offset;
   }

   public int get_height_offset() {
      return this.height_offset;
   }

   public int get_width_size() {
      return this.width_size;
   }

   public int get_height_size() {
      return this.height_size;
   }

   public int get_width_offset_by_screen_size(int screen_width) {
      return (int)((float)screen_width * (float)this.get_width_offset() * 0.01F);
   }

   public int get_height_offset_by_screen_size(int screen_height) {
      return (int)((float)screen_height * (float)this.get_height_offset() * 0.01F);
   }

   public int get_width_size_by_screen_size(int screen_width) {
      return (int)((float)screen_width * (float)this.get_width_size() * 0.01F);
   }

   public int get_height_size_by_screen_size(int screen_height) {
      return (int)((float)screen_height * (float)this.get_height_size() * 0.01F);
   }

   public void set_at_center_of_screen_by_size() {
      this.set_width_offset(50 - this.width_size / 2);
      this.set_height_offset(50 - this.height_size / 2);
   }
}
