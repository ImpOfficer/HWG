package mod.azure.hwg.config;

import mod.azure.azurelib.config.Config;
import mod.azure.azurelib.config.Configurable;
import mod.azure.hwg.HWGMod;

@Config(id = HWGMod.MODID)
public class HWGConfig {

	@Configurable
	public MobConfigs mobconfigs = new MobConfigs();

	public static class MobConfigs {

		@Configurable
		public MercConfigs mercconfigs = new MercConfigs();

		public static class MercConfigs {
			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int merc_spawn_weight = 5;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int merc_min_group = 1;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int merc_max_group = 2;

			@Configurable
			@Configurable.Synchronized
			@Configurable.DecimalRange(min = 1)
			public double merc_health = 20;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int merc_exp = 4;
		}

		@Configurable
		public SpyConfigs spyconfigs = new SpyConfigs();

		public static class SpyConfigs {
			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int spy_spawn_weight = 5;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int spy_min_group = 1;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int spy_max_group = 2;

			@Configurable
			@Configurable.Synchronized
			@Configurable.DecimalRange(min = 1)
			public double spy_health = 20;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int spy_exp = 4;
		}

		@Configurable
		public LesserConfigs lesserconfigs = new LesserConfigs();

		public static class LesserConfigs {
			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int lesser_spawn_weight = 2;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int lesser_min_group = 1;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int lesser_max_group = 2;

			@Configurable
			@Configurable.Synchronized
			@Configurable.DecimalRange(min = 1)
			public double lesser_health = 48;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int lesser_exp = 8;
		}

		@Configurable
		public GreatConfigs greatconfigs = new GreatConfigs();

		public static class GreatConfigs {
			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int greater_spawn_weight = 1;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int greater_min_group = 1;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int greater_max_group = 1;

			@Configurable
			@Configurable.Synchronized
			@Configurable.DecimalRange(min = 1)
			public double greater_health = 100;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int greater_exp = 16;
		}
	}

	@Configurable
	public GunConfigs gunconfigs = new GunConfigs();

	public static class GunConfigs {

		@Configurable
		@Configurable.Synchronized
		public boolean rocket_breaks = true;
		@Configurable
		@Configurable.Synchronized
		public boolean balrog_breaks = false;
		@Configurable
		@Configurable.Synchronized
		public boolean grenades_breaks = false;
		@Configurable
		@Configurable.Synchronized
		public boolean bullets_breakdripstone = false;
		@Configurable
		@Configurable.Synchronized
		public boolean bullets_disable_iframes_on_players = false;

		@Configurable
		public FlammerConfigs flammerconfigs = new FlammerConfigs();

		public static class FlammerConfigs {
			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int flammer_cap = 250;
		}

		@Configurable
		public PistolConfigs pistolconfigs = new PistolConfigs();

		public static class PistolConfigs {
			@Configurable
			@Configurable.Synchronized
			@Configurable.DecimalRange(min = 1)
			public float pistol_damage = 5F;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int pistol_cap = 5;
		}

		@Configurable
		public GPistolConfigs gpistolconfigs = new GPistolConfigs();

		public static class GPistolConfigs {
			@Configurable
			@Configurable.Synchronized
			@Configurable.DecimalRange(min = 1)
			public float golden_pistol_damage = 6F;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int golden_pistol_cap = 5;
		}

		@Configurable
		public SPistolConfigs silencedpistolconfigs = new SPistolConfigs();

		public static class SPistolConfigs {
			@Configurable
			@Configurable.Synchronized
			@Configurable.DecimalRange(min = 1)
			public float silenced_pistol_damage = 4F;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int silenced_pistol_cap = 6;
		}

		@Configurable
		public LugerConfigs lugerconfigs = new LugerConfigs();

		public static class LugerConfigs {
			@Configurable
			@Configurable.Synchronized
			@Configurable.DecimalRange(min = 1)
			public float luger_damage = 5.5F;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int luger_cap = 6;
		}

		@Configurable
		public AKConfigs ak47configs = new AKConfigs();

		public static class AKConfigs {
			@Configurable
			@Configurable.Synchronized
			@Configurable.DecimalRange(min = 1)
			public float ak47_damage = 6F;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int ak47_cap = 20;
		}

		@Configurable
		public SniperConfigs sniperconfigs = new SniperConfigs();

		public static class SniperConfigs {
			@Configurable
			@Configurable.Synchronized
			@Configurable.DecimalRange(min = 1)
			public float sniper_damage = 25F;

			@Configurable
			@Configurable.Synchronized
			@Configurable.DecimalRange(min = 1)
			public float sniper_scoped_damage = 30F;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int sniper_cap = 2;
		}

		@Configurable
		public MinigunConfigs minigunconfigs = new MinigunConfigs();

		public static class MinigunConfigs {
			@Configurable
			@Configurable.Synchronized
			@Configurable.DecimalRange(min = 1)
			public float minigun_damage = 4F;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int minigun_cap = 100;
		}

		@Configurable
		public SMGConfigs smgconfigs = new SMGConfigs();

		public static class SMGConfigs {
			@Configurable
			@Configurable.Synchronized
			@Configurable.DecimalRange(min = 1)
			public float smg_damage = 3.5F;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int smg_cap = 50;
		}

		@Configurable
		public HellhorseConfigs hellhorseconfigs = new HellhorseConfigs();

		public static class HellhorseConfigs {
			@Configurable
			@Configurable.Synchronized
			@Configurable.DecimalRange(min = 1)
			public float hellhorse_damage = 5F;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int hellhorse_cap = 6;
		}

		@Configurable
		public TommyConfigs tommyconfigs = new TommyConfigs();

		public static class TommyConfigs {
			@Configurable
			@Configurable.Synchronized
			@Configurable.DecimalRange(min = 1)
			public float tommy_damage = 3F;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int tommy_cap = 50;
		}

		@Configurable
		public ShotgunConfigs shotgunconfigs = new ShotgunConfigs();

		public static class ShotgunConfigs {
			@Configurable
			@Configurable.Synchronized
			@Configurable.DecimalRange(min = 1)
			public float shotgun_damage = 5F;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int shotgun_cap = 2;
		}

		@Configurable
		public MeanieConfigs meanieconfigs = new MeanieConfigs();

		public static class MeanieConfigs {
			@Configurable
			@Configurable.Synchronized
			@Configurable.DecimalRange(min = 1)
			public float meanie_damage = 6F;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int meanie_cap = 6;
		}

		@Configurable
		public BalrogConfigs balrogconfigs = new BalrogConfigs();

		public static class BalrogConfigs {
			@Configurable
			@Configurable.Synchronized
			@Configurable.DecimalRange(min = 1)
			public float balrog_damage = 8F;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int balrog_cap = 4;
		}

		@Configurable
		public BrimstoneConfigs brimstoneconfigs = new BrimstoneConfigs();

		public static class BrimstoneConfigs {
			@Configurable
			@Configurable.Synchronized
			@Configurable.DecimalRange(min = 1)
			public float brimstone_damage = 3F;

			@Configurable
			@Configurable.Synchronized
			@Configurable.Range(min = 1)
			public int brimstone_cap = 185;
		}
	}
}