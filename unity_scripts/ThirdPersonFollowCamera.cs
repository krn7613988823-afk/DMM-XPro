// NINJA BATTLE ROYALE: SHADOW LEGENDS
// Unity 3D C# Third Person Follow Camera with Collision Avoidance

using UnityEngine;

namespace NinjaBattleRoyale
{
    public class ThirdPersonFollowCamera : MonoBehaviour
    {
        public Transform target;
        public float distance = 5.5f;
        public float heightOffset = 2.2f;
        public float shoulderOffset = 0.8f;
        public float sensitivityX = 2.5f;
        public float sensitivityY = 1.8f;
        public float minPitch = -25f;
        public float maxPitch = 60f;

        public LayerMask obstacleLayers;

        private float currentYaw = 0f;
        private float currentPitch = 15f;
        private float currentDist;

        void Start()
        {
            currentDist = distance;
            Cursor.lockState = CursorLockMode.Locked;
        }

        void LateUpdate()
        {
            if (!target) return;

            currentYaw += Input.GetAxis("Mouse X") * sensitivityX;
            currentPitch -= Input.GetAxis("Mouse Y") * sensitivityY;
            currentPitch = Mathf.Clamp(currentPitch, minPitch, maxPitch);

            Quaternion rotation = Quaternion.Euler(currentPitch, currentYaw, 0);

            Vector3 targetCenter = target.position + Vector3.up * heightOffset;
            Vector3 desiredOffset = rotation * new Vector3(shoulderOffset, 0, -distance);
            Vector3 desiredPos = targetCenter + desiredOffset;

            // Camera collision avoidance
            RaycastHit hit;
            if (Physics.SphereCast(targetCenter, 0.25f, (desiredPos - targetCenter).normalized, out hit, distance, obstacleLayers))
            {
                currentDist = Mathf.Clamp(hit.distance - 0.2f, 1.2f, distance);
            }
            else
            {
                currentDist = distance;
            }

            Vector3 finalOffset = rotation * new Vector3(shoulderOffset, 0, -currentDist);
            transform.position = targetCenter + finalOffset;
            transform.LookAt(targetCenter + rotation * new Vector3(shoulderOffset, 0, 0));
        }
    }
}
