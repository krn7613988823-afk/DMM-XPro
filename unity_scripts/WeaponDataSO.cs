// NINJA BATTLE ROYALE: SHADOW LEGENDS
// Unity 3D C# ScriptableObject Weapon Definitions (All 21 Weapons)

using UnityEngine;

namespace NinjaBattleRoyale
{
    public enum WeaponType
    {
        Melee,
        Throwable,
        Firearm,
        Special
    }

    [CreateAssetMenu(fileName = "NewWeapon", menuName = "Ninja/WeaponData")]
    public class WeaponDataSO : ScriptableObject
    {
        public string weaponId;
        public string weaponName;
        public WeaponType type;
        public float damage;
        public float rangeMeters;
        public float attackCooldownSeconds;
        public int magazineSize;
        public float reloadTimeSeconds;
        public int priceTaka;
        [TextArea] public string description;
        public GameObject weaponPrefab;
        public AudioClip attackAudio;
    }
}
