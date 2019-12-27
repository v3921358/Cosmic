import org.apache.hadoop.fs._
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

// Maplestory Constants
val ExpPerLvl = Array(1, 15, 34, 57, 92, 135, 372, 560, 840, 1144, 1242, 1573, 2144, 2800, 3640, 4700, 5893, 7360, 9144, 11120, 13477, 16268, 19320, 22880, 27008, 31477, 36600, 42444, 48720, 55813, 63800, 86784, 98208, 110932, 124432, 139372, 155865, 173280, 192400, 213345, 235372, 259392, 285532, 312928, 342624, 374760, 408336, 445544, 483532, 524160, 567772, 598886, 631704, 666321, 702836, 741351, 781976, 824828, 870028, 917625, 967995, 1021041, 1076994, 1136013, 1198266, 1263930, 1333194, 1406252, 1483314, 1564600, 1650340, 1740778, 1836173, 1936794, 2042930, 2154882, 2272970, 2397528, 2528912, 2667496, 2813674, 2967863, 3130502, 3302053, 3483005, 3673873, 3875201, 4087562, 4311559, 4547832, 4797053, 5059931, 5337215, 5629694, 5938202, 6263614, 6606860, 6968915, 7350811, 7753635, 8178534, 8626718, 9099462, 9598112, 10124088, 10678888, 11264090, 11881362, 12532461, 13219239, 13943653, 14707765, 15513750, 16363902, 17260644, 18206527, 19204245, 20256637, 21366700, 22537594, 23772654, 25075395, 26449526, 27898960, 29427822, 31040466, 32741483, 34535716, 36428273, 38424542, 40530206, 42751262, 45094030, 47565183, 50171755, 52921167, 55821246, 58880250, 62106888, 65510344, 69100311, 72887008, 76881216, 81094306, 85594273, 90225770, 95170142, 100385466, 105886589, 111689174, 117809740, 124265714, 131075474, 138258410, 145834970, 153826726, 162256430, 171148082, 180526997, 190419876, 200854885, 211861732, 223471711, 223471711, 248635353, 262260570, 276632449, 291791906, 307782102, 324648562, 342439302, 361204976, 380999008, 401877754, 423900654, 447130410, 471633156, 497478653, 524740482, 553496261, 583827855, 615821622, 649568646, 685165008, 722712050, 762316670, 804091623, 848155844, 894634784, 943660770, 995373379, 1049919840, 1107455447, 1168144006, 1232158297, 1299680571, 1370903066, 1446028554, 1525246918, 1608855764, 1697021059)
val LevelGroup = Array("1 - 4", "5 - 9", "10 - 14", "15 - 19", "20 - 24", "25 - 29", "30 - 34", "35 - 39", "40 - 44", "45 - 49", "50 - 54", "55 - 59", "60 - 64", "65 - 69", "70 - 74", "75 - 79", "80 - 84", "85 - 89", "90 - 94", "95 - 99", "100 - 104", "105 - 109", "110 - 114", "120 - 124", "125 - 129", "130 - 134", "135 - 139", "140 - 144", "145 - 149", "150 - 154", "155 - 159", "160 - 164",  "165 - 169", "170 - 174", "175 - 179", "180 - 184", "185 - 189", "190 - 194", "195 - 200")
// Data Definition
case class MapleMap(
	id: Int,
	name: String,
	mob_count: Int,
	median_lvl: Int,
	mean_exp: Double,
	exp_per_min: Int,
	boss_exp: Int,
	spread: String,
	travel_difficulty: String,
	comments: String,
	suggestions: String
)

case class MapByLevelGroup(
	num_maps: Int,
	min_mob_count: Int,
	max_mob_count: Int,
	mob_count_sum: Int,
	min_mean_exp: Int,
	max_mean_exp: Int,
	sum_mean_exp: Int,
	min_exp_min: Int,
	max_exp_min: Int,
	sum_exp_min: Int,
	thin_spread_freq: Int,
	medium_spread_freq: Int,
	dense_spread_freq: Int,
	easy_travel_freq: Int,
	medium_travel_freq: Int,
	difficult_travel_freq: Int
)

// Read Datafile
val filename = args(0)
val textFile = sc.textFile(filename)

// Compute Normalized Data
val normalized = textFile.map(line => {
					val data = line.split(";", -1)
					val mapData = MapleMap(data(0).toInt, 
						data(1), 
						data(2).toInt, 
						data(3).toInt, 
						data(4).toDouble, 
						data(5).toInt, 
						if (data(10) == "") 0 else data(10).toInt, 
						data(6), 
						data(7), 
						data(8), 
						data(9))
					(
						mapData.median_lvl/5, 
						MapByLevelGroup(
							1,
							mapData.mob_count,
							mapData.mob_count,
							mapData.mob_count,
							mapData.mean_exp.toInt,
							mapData.mean_exp.toInt,
							mapData.mean_exp.toInt,
							mapData.exp_per_min,
							mapData.exp_per_min,
							mapData.exp_per_min,
							if(mapData.spread.toLowerCase().contains("thin")) 1 else 0,
							if(mapData.spread.toLowerCase().contains("medium")) 1 else 0,
							if(mapData.spread.toLowerCase().contains("dense")) 1 else 0,
							if(mapData.travel_difficulty.toLowerCase().contains("easy")) 1 else 0,
							if(mapData.travel_difficulty.toLowerCase().contains("medium")) 1 else 0,
							if(mapData.travel_difficulty.toLowerCase().contains("difficult")) 1 else 0
						)
					)
				})
				.reduceByKey((accum: MapByLevelGroup, mapData: MapByLevelGroup) => {
					val nextAccum = MapByLevelGroup(
						accum.num_maps + mapData.num_maps,
						if(accum.min_mob_count < mapData.min_mob_count) accum.min_mob_count else mapData.min_mob_count,
						if(accum.max_mob_count > mapData.max_mob_count) accum.max_mob_count else mapData.max_mob_count,
						accum.mob_count_sum + mapData.mob_count_sum,
						if(accum.min_mean_exp < mapData.min_mean_exp) accum.min_mean_exp else mapData.min_mean_exp,
						if(accum.max_mean_exp > mapData.max_mean_exp) accum.max_mean_exp else mapData.max_mean_exp,
						accum.sum_mean_exp + mapData.sum_mean_exp,
						if(accum.min_exp_min < mapData.min_exp_min) accum.min_exp_min else mapData.min_exp_min,
						if(accum.max_exp_min > mapData.max_exp_min) accum.max_exp_min else mapData.max_exp_min,
						accum.sum_exp_min + mapData.sum_exp_min,
						accum.thin_spread_freq + mapData.thin_spread_freq,
						accum.medium_spread_freq + mapData.medium_spread_freq,
						accum.dense_spread_freq + mapData.dense_spread_freq,
						accum.easy_travel_freq + mapData.easy_travel_freq,
						accum.medium_travel_freq + mapData.medium_travel_freq,
						accum.difficult_travel_freq + mapData.difficult_travel_freq
					)
					(nextAccum)
				})
				.sortBy(_._1)
				.map(tuple => {
					val num_maps = tuple._2.num_maps
					List (
						LevelGroup(tuple._1),
						num_maps,
						// Mob Count Data
						tuple._2.min_mob_count,
						tuple._2.max_mob_count,
						tuple._2.mob_count_sum / num_maps,
						// Mean Exp Data
						tuple._2.min_mean_exp,
						tuple._2.max_mean_exp,
						tuple._2.sum_mean_exp / num_maps,
						// Mean Exp as % of Level
						tuple._2.min_mean_exp.toDouble * 100 / ExpPerLvl(tuple._1 * 5 + 2),
						tuple._2.max_mean_exp.toDouble * 100 / ExpPerLvl(tuple._1 * 5 + 2),
						(tuple._2.sum_mean_exp / num_maps).toDouble * 100 / ExpPerLvl(tuple._1 * 5 + 2),
						// Number of kills to Level
						ExpPerLvl(tuple._1 * 5 + 2) / tuple._2.min_mean_exp.toDouble,
						ExpPerLvl(tuple._1 * 5 + 2) / tuple._2.max_mean_exp.toDouble,
						ExpPerLvl(tuple._1 * 5 + 2) / (tuple._2.sum_mean_exp / num_maps).toDouble,
						// Exp per min Data
						tuple._2.min_exp_min,
						tuple._2.max_exp_min,
						tuple._2.sum_exp_min / num_maps,
						// Exp per min as % of Level
						tuple._2.min_exp_min.toDouble * 100 / ExpPerLvl(tuple._1 * 5 + 2),
						tuple._2.max_exp_min.toDouble * 100 / ExpPerLvl(tuple._1 * 5 + 2),
						(tuple._2.sum_exp_min / num_maps).toDouble * 100 / ExpPerLvl(tuple._1 * 5 + 2),
						// Time (in minutes) to level
						ExpPerLvl(tuple._1 * 5 + 2) / tuple._2.min_exp_min.toDouble,
						ExpPerLvl(tuple._1 * 5 + 2) / tuple._2.max_exp_min.toDouble,
						ExpPerLvl(tuple._1 * 5 + 2) / (tuple._2.sum_exp_min / num_maps).toDouble,
						// Density spread data
						tuple._2.thin_spread_freq,
						tuple._2.medium_spread_freq,
						tuple._2.dense_spread_freq,
						// Training difficulty data
						tuple._2.easy_travel_freq,
						tuple._2.medium_travel_freq,
						tuple._2.difficult_travel_freq
					)
				})

normalized.collect().foreach(println)
