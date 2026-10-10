// NINJA BATTLE ROYALE: SHADOW LEGENDS
// Unity 3D C# Character Controller (URP, Android & Desktop)

using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace NinjaBattleRoyale
{
    [RequireComponent(typeof(CharacterController))]
    public class NinjaCharacterController : MonoBehaviour
    {
        [Header("Movement")]
        public float walkSpeed = 6.0f;
        public float sprintSpeed = 9.5f;
        public float crouchSpeed = 3.5f;
        public float jumpForce = 8.5f;
        public float gravity = -24.0f;

        [Header("Stats")]
        public float maxHealth = 200f;
        public float currentHealth = 200f;
        public float maxArmor = 100f;
        public float currentArmor = 100f;
        public float maxEnergy = 200f;
        public float currentEnergy = 150f;

        [Header("Combat & Weapons")]
        public Transform weaponGripPoint;
        public GameObject currentWeaponObject;
        public bool isBlocking = false;
        public bool isShadowCloaked = false;

        private CharacterController controller;
        private Vector3 velocity;
        private bool isGrounded;
        private Animator animator;

        void Awake()
        {
            controller = GetComponent<CharacterController>();
            animator = GetComponentInChildren<Animator>();
        }

        void Update()
        {
            isGrounded = controller.isGrounded;
            if (isGrounded && velocity.y < 0)
            {
                velocity.y = -2f;
            }

            float h = Input.GetAxis("Horizontal");
            float v = Input.GetAxis("Vertical");
            Vector3 inputDir = new Vector3(h, 0, v).normalized;

            if (inputDir.magnitude >= 0.1f)
            {
                float targetAngle = Mathf.Atan2(inputDir.x, inputDir.z) * Mathf.Rad2Deg + Camera.main.transform.eulerAngles.y;
                transform.rotation = Quaternion.Euler(0f, targetAngle, 0f);

                Vector3 moveDir = Quaternion.Euler(0f, targetAngle, 0f) * Vector3.forward;
                float currentSpeed = Input.GetKey(KeyCode.LeftShift) ? sprintSpeed : walkSpeed;
                controller.Move(moveDir.normalized * currentSpeed * Time.deltaTime);
            }

            if (Input.GetButtonDown("Jump") && isGrounded)
            {
                velocity.y = Mathf.Sqrt(jumpForce * -2f * gravity);
            }

            // Shadow Dash
            if (Input.GetKeyDown(KeyCode.Q) && currentEnergy >= 25f)
            {
                currentEnergy -= 25f;
                controller.Move(transform.forward * 8f);
            }

            velocity.y += gravity * Time.deltaTime;
            controller.Move(velocity * Time.deltaTime);
        }

        public void TakeDamage(float amount, bool isHeadshot = false)
        {
            float finalDmg = isHeadshot ? amount * 2f : amount;
            if (isBlocking) finalDmg *= 0.35f;

            if (currentArmor > 0)
            {
                float armorAbsorb = finalDmg * 0.6f;
                currentArmor = Mathf.Max(0, currentArmor - armorAbsorb);
                finalDmg -= armorAbsorb;
            }

            currentHealth = Mathf.Max(0, currentHealth - finalDmg);
            if (currentHealth <= 0)
            {
                Die();
            }
        }

        private void Die()
        {
            if (animator != null) animator.SetTrigger("Knockout");
            enabled = false;
        }
    }
}
