package com.example.flywheel_of_terror.mixin;

import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer.FoliageAttachment;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer.FoliageSetter;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(
   value = {TreeFeature.class},
   priority = 10000
)
public class less_trees {
   private static Random random = new Random();

   @Overwrite
   private boolean doPlace(
      WorldGenLevel p_225258_,
      RandomSource p_225259_,
      BlockPos p_225260_,
      BiConsumer<BlockPos, BlockState> p_225261_,
      BiConsumer<BlockPos, BlockState> p_225262_,
      FoliageSetter p_273670_,
      TreeConfiguration p_225264_
   ) {
      if (random.nextInt(1, 30) != 20) {
         return false;
      } else {
         int i = p_225264_.trunkPlacer.getTreeHeight(p_225259_);
         int j = p_225264_.foliagePlacer.foliageHeight(p_225259_, i, p_225264_);
         int k = i - j;
         int l = p_225264_.foliagePlacer.foliageRadius(p_225259_, k);
         BlockPos blockpos = p_225264_.rootPlacer.<BlockPos>map(p_225286_ -> p_225286_.getTrunkOrigin(p_225260_, p_225259_)).orElse(p_225260_);
         int i1 = Math.min(p_225260_.getY(), blockpos.getY());
         int j1 = Math.max(p_225260_.getY(), blockpos.getY()) + i + 1;
         if (i1 >= p_225258_.getMinBuildHeight() + 1 && j1 <= p_225258_.getMaxBuildHeight()) {
            OptionalInt optionalint = p_225264_.minimumSize.minClippedHeight();
            int k1 = 7;
            if (k1 >= i || !optionalint.isEmpty() && k1 >= optionalint.getAsInt()) {
               if (p_225264_.rootPlacer.isPresent()
                  && !((RootPlacer)p_225264_.rootPlacer.get()).placeRoots(p_225258_, p_225261_, p_225259_, p_225260_, blockpos, p_225264_)) {
                  return false;
               } else {
                  List<FoliageAttachment> list = p_225264_.trunkPlacer.placeTrunk(p_225258_, p_225262_, p_225259_, k1, blockpos, p_225264_);
                  list.forEach(p_272582_ -> p_225264_.foliagePlacer.createFoliage(p_225258_, p_273670_, p_225259_, p_225264_, k1, p_272582_, j, l));
                  return true;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }
}
